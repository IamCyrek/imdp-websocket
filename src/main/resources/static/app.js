var stompClient = null;
var room = 0;
var userName = "";

var isFirstTyping = true;
var typingTimer;
var doneTypingInterval = 3000;

function hideRoom() {
    $("#roomForm").css("display", "none");
}

function hideConnection() {
    $("#connectionForm").css("display", "none");
}

function hideName() {
    $("#name").css("display", "none");
}

function hideMessage() {
    $("#message").css("display", "none");
}

function hideConversation() {
    $("#typing").hide();
    $("#conversation").hide();
}

function hideRight() {
    hideName();
    hideMessage();
    hideConversation();
}

function showConnection() {
    hideRoom();

    $("#connectionForm").css("display", "block");
}

function showRoom() {
    hideConnection();

    $("#roomForm").css("display", "block");
}

function showName() {
    hideRight();

    $("#name").css("display", "block");
}

function showMessage() {
    hideRight();
    showConversation();

    $("#message").css("display", "block");
}

function showConversation() {
    $("#typing").show();
    $("#conversation").show();
}

function showTyping(text) {
    $("#typing").text(text);
}

function showMessageInTable(message) {
    $("<tr>" +
      "<td>" + message.userName + "</td>" +
      "<td>" + message.content + "</td>" +
      "</tr>").prependTo("#messages");
}

function setInputForm(connected) {
    if (connected) {
        showName();

        $("#inputTextName").val(userName);
    } else {
        hideRight();
    }
}

function connect() {
    var socket = new SockJS('/websocket-example');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setInputForm(true);

        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages/' + room, function (message) {
            showMessageInTable(JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/typing/' + room, function (message) {
            var names = JSON.parse(message.body);
            names = $.grep(names, function (name) {
                return userName !== name;
            });

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

        var hello = stompClient.subscribe('/topic/hello/' + room, function (messages) {
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

    $("#messages").html("");

    setInputForm(false);

    showRoom();

    console.log("Disconnected");
}

function chooseRoom() {
    room = $("#room").val();

    showConnection();
    connect();
}

function sendName() {
    userName = $("#inputTextName").val();

    stompClient.send("/app/hello/" + room, {}, JSON.stringify(
        {
            'name': userName,
            'creationTime': +new Date()
        }));

    showMessage();
}

function sendMessage() {
    stompClient.send("/app/messaging/" + room, {}, JSON.stringify(
        {
            'userName': userName,
            'content': $("#inputText").val(),
            'creationTime': +new Date()
        }));

    $("#inputText").val("");
}

function sendTyping(isTyping) {
    stompClient.send("/app/typing/" + room, {}, JSON.stringify(
        {
            'userName': userName,
            'isTyping': isTyping
        }));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#chooseRoom").click(function () {
        chooseRoom();
    });
    $("#sendName").click(function () {
        sendName();
    });
    $("#send").click(function () {
        sendMessage();
    });

    hideConnection();
    hideRight();

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
        typingTimer = setTimeout(doneTyping, doneTypingInterval);
    });

    function doneTyping() {
        isFirstTyping = true;

        sendTyping(false);
    }
});
