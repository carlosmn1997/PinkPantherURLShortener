$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });


$(function(){
    $("#uploadCsv").on("submit", function(e){
        e.preventDefault();
        var f = $(this);
        var formData = new FormData(document.getElementById("uploadCsv"));
        //formData.append("dato", "valor"); Si queremos añadir mas cosas
        //formData.append(f.attr("name"), $(this)[0].files[0]);
        $.ajax({
            url: "/uploadCSV",
            type: "post",
            dataType: "html",
            data: formData,
            cache: false,
            contentType: false,
            processData: false
        })
            .done(function(res){
                //$("#mensaje").html("Respuesta: " + res);
                console.log(res);
                getCsvStatus(res);
            });
    });
});

function getCsvStatus(url){
    $.ajax({
        url: url
    })
        .done(function(res){
            console.log(res);
            setTimeout(getCsvStatus(url), 50000);
        });
}

/*
(function getCsvStatus(id) {
    $.ajax({
        url: '/job/'+id,
        success: function(data) {
            $('.result').html(data);
        },
        complete: function() {
            // Schedule the next request when the current one's complete
            setTimeout(getCsvStatus(id), 5000);
        }
    });
})();

*/
