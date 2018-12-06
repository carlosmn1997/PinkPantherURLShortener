var stompClient = null;

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function connect() {
    var socket = new SockJS('/waitingSponsor');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        var token = $("#token").text();
        stompClient.subscribe('/queue/reply-' + token, function (message) {
            console.log("Message " + message.body);
            //TODO: no redirigir automaticamente, poner enlace en boton nuevo, etc
            window.location.replace(message.body);
            disconnect()
        });
        var hash = window.location.pathname.replace("/", "");
        stompClient.send('/app/waitingSponsor', {}, JSON.stringify(
            {content: hash, idTimer: token}
        ));
    });
}

$(document).ready(
    function () {
        $("#sponsor-skip-button").click(function () {
            $("#sponsor-skip-button").hide();
            $("#sponsor-countdown").show();
            connect();
        });
    }
);