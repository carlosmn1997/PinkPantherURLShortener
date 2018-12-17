$(document).ready(
    function () {
        $("#alive").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "GET",
                    url: "/" + $("#hash").val() +"/alive",
                    success: function (msg) {
                        if(msg == true) {
                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank'>"
                                + "The url is alive"
                                + "</a></div>");
                        }
                        else {
                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank'>"
                                + "The url is not alive"
                                + "</a></div>");
                        }
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