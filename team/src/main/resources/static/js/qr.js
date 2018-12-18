$(document).ready(
    function () {
        $("#qr").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "GET",
                    url: "/" + $("#hash").val() +"/qr",
                    success: function (msg) {
                        $("#result").html(
                            "<img alt=\"Your qr\" src=\"data:image/png;base64,"+ msg + "\"/>"
                        );
                    },
                    error: function (msg) {
                        $("#result").html(
                            "<div class='alert alert-danger lead'><a target='_blank'>"
                            + msg.responseText
                            + "</a></div>");
                    },
                });
            });
    }
);