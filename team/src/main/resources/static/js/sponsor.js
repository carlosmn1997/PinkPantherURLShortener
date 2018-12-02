$(document).ready(
    function() {
        $("#sponsor-skip-button").click(function(){
            $("#sponsor-skip-button").hide();
            $("#sponsor-countdown").show();
            // TODO: websockets interaction
        });
    }
);




var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        //stompClient.subscribe('user/queue/reply', function (greeting) {
        //    showGreeting(JSON.parse(greeting.body).content);
        //});
        //stompClient.subscribe('user/queue/reply', function(message) {
        //    alert("Message " + message.body);
        //});
        stompClient.subscribe('/queue/reply-pepa', function(message) {
            alert("Message " + message.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    //stompClient.send("/app/hello", {}, "pinche pendejo");
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});