$(document).ready(
    function() {
        $("#sponsor-skip-button").click(function(){
            $("#sponsor-skip-button").hide();
            $("#sponsor-countdown").show();
            // TODO: websockets interaction
        });
    }
);