$(document).ready(
    function () {
        $("#alive").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "GET",
                    url: "/" + $("#hash").val() +"/alive",
                    success: function (msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank'>"
                            + msg
                            + "</a></div>");
                    },
                    error: function (msg) {
                        $("#result").html(
                            "<div class='alert alert-danger lead'><a target='_blank'>"
                            + JSON.parse(msg.responseText).message
                            + "</a></div>");
                    },
                });
            });
    }
);