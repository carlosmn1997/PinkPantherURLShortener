var stompClient = null;
var secondsLeft = 5;

function startCountdown(){
    secondsLeft = 5;
    setInterval(function(){
        secondsLeft = secondsLeft - 1;
        if(secondsLeft>=0){
            $("#sponsor-countdown").text("Wait "+secondsLeft+" seconds...");
        }
    }, 1000);
}

function setGoButton(newb,oldb,href){
    newb.click(function(){window.location.replace(href)});
    oldb.hide();
    newb.show();
}

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
            setGoButton($("#sponsor-go-button"),$("#sponsor-countdown"),message.body);
            disconnect();
        });

        var hash = window.location.pathname.replace("/", "");
        stompClient.send('/app/waitingSponsor', {}, JSON.stringify(
            {content: hash, idTimer: token}
        ));

        startCountdown();
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