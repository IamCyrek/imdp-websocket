var stompClient = null;
var userName = "";
var isNameEntering = true;

function hide() {
    $("#conversation").hide();
    $("#inputLabel").hide();
    $("#inputText").hide();
    $("#send").hide();
}

function show() {
    $("#conversation").show();
    $("#inputLabel").show();
    $("#inputText").show();
    $("#send").show();
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#messages").html("");
}

function setInputForm(connected) {
    if (connected) {
        show();

        isNameEntering = true;
        $("#inputLabel").html("What is your name?");
        $("#inputText").attr('maxlength','63');
        $("#inputText").val(userName);
    } else {
        hide();
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
            showMessage(JSON.parse(message.body));
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
    if (isNameEntering) {
        stompClient.send("/app/hello", {}, JSON.stringify(
            {
                'name': $("#inputText").val(),
                'creationTime': +new Date()
            }));

        isNameEntering = false;
        $("#inputLabel").html("Type your message");
        userName = $("#inputText").val();
        $("#inputText").attr('maxlength','255');
    } else {
        stompClient.send("/app/messaging", {}, JSON.stringify(
            {
                'userName': userName,
                'content': $("#inputText").val(),
                'creationTime': +new Date()
            }));
    }

    $("#inputText").val("");
}

function showMessage(message) {
    $("#messages").append(
        "<tr>" +
            "<td>" + message.userName + "</td>" +
            "<td>" + message.content + "</td>" +
        "</tr>"
    );
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });

    hide();
});
