var stompClient = null;
var userName = "";

var isFirstTyping = true;
var typingTimer;
var doneTypingInterval = 3000;

function hideName() {
    $("#inputLabelName").hide();
    $("#inputTextName").hide();
    $("#sendName").hide();
}

function hideMessage() {
    $("#inputLabel").hide();
    $("#inputText").hide();
    $("#send").hide();
}

function hideConversation() {
    $("#typing").hide();
    $("#conversation").hide();
}

function hideAll() {
    hideName();
    hideMessage();
    hideConversation();
}

function showName() {
    hideMessage();

    $("#inputLabelName").show();
    $("#inputTextName").show();
    $("#sendName").show();
}

function showMessage() {
    hideName();

    $("#inputLabel").show();
    $("#inputText").show();
    $("#send").show();
}

function showConversation() {
    $("#typing").show();
    $("#conversation").show();
}

function showTyping(text) {
    $("#typing").text(text);
}

function showMessageInTable(message) {
    $("#messages").append(
        "<tr>" +
        "<td>" + message.userName + "</td>" +
        "<td>" + message.content + "</td>" +
        "</tr>"
    );
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#messages").html("");
}

function setInputForm(connected) {
    if (connected) {
        showName();
        showConversation();

        $("#inputTextName").val(userName);
    } else {
        hideAll();
    }
}

function connect() {
    var socket = new SockJS('/websocket-example');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        setInputForm(true);

        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages', function (message) {
            showMessageInTable(JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/typing', function (message) {
            var names = JSON.parse(message.body);
            names = $.grep(names, function (name) {
                return userName !== name;
            });
            console.log(names);

            if (names.length === 0) {
                showTyping("");
            } else if (names.length === 1) {
                showTyping(names + " is typing...");
            } else if (names.length === 2) {
                showTyping(names[0] + " and " + names[1] + " are typing...");
            } else {
                showTyping("3 and more are typing...");
            }
        });

        var hello = stompClient.subscribe('/topic/hello', function (messages) {
            $(JSON.parse(messages.body)).each(function (idx, obj) {
                showMessageInTable(obj);
            });
            hello.unsubscribe();
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }

    setConnected(false);
    setInputForm(false);

    console.log("Disconnected");
}

function sendName() {
    userName = $("#inputTextName").val();

    stompClient.send("/app/hello", {}, JSON.stringify(
        {
            'name': userName,
            'creationTime': +new Date()
        }));

    showMessage();
}

function sendMessage() {
    stompClient.send("/app/messaging", {}, JSON.stringify(
        {
            'userName': userName,
            'content': $("#inputText").val(),
            'creationTime': +new Date()
        }));

    $("#inputText").val("");
}

function sendTyping(isTyping) {
    stompClient.send("/app/typing", {}, JSON.stringify(
        {
            'userName': userName,
            'isTyping': isTyping
        }));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#sendName").click(function () {
        sendName();
    });
    $("#send").click(function () {
        sendMessage();
    });

    hideAll();

    $("#inputTextName").attr('maxlength', '63');
    $("#inputText").attr('maxlength', '255');

    $("#inputText").on('keyup', function () {
        if (isFirstTyping) {
            isFirstTyping = false;

            sendTyping(true);
        }

        clearTimeout(typingTimer);
        typingTimer = setTimeout(doneTyping, doneTypingInterval);
    });

    $("#inputText").on('keydown', function () {
        clearTimeout(typingTimer);
    });

    function doneTyping() {
        isFirstTyping = true;

        sendTyping(false);
    }
});
