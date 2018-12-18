$(function () {
    $("#uploadCsv").on("submit", function (e) {
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
            processData: false,
            statusCode: {
                400: function (response) {
                    alert('Fichero csv malformado');
                    bootbox.alert('<span style="color:Red;">Fichero csv malformado</span>', function () { });
                }
            },
            success: function(data, status, xhr) {
                console.log(xhr.getResponseHeader('Location'));
                var location = xhr.getResponseHeader('Location');
                $("#myModal").modal();
                getCsvStatus(location);
            }
        })
    });
});

function getCsvStatus(url) {
    $.ajax({
        url: url
    })
        .done(function (res) {
            //aria-valuenow="50";
            //style="width:50%"
            var percentage = (res.converted / res.total)*100
            //document.getElementById("progressBar").aria-valuenow;
            document.getElementById("progressBar").style = "width:"+percentage+'%';
            document.getElementById("progressBar").innerHTML = res.converted + " de " + res.total;
            if(res.uriResult != null){ // Conversion finished
                var win = window.open(res.uriResult, '_blank');
                win.focus();
            } else {
                setTimeout(getCsvStatus(url), 5000);
            }

        });
}