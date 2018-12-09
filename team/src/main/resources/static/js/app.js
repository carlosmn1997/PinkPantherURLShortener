$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/short",
                    data: $("#shortener").serialize({checkboxesAsBools: true}),
                    success: function (msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
        $("#alive").submit(
            function (event) {
                event.preventDefault();
                var url = "/" + this.value +"/alive";
                alert(url + " Gola");
                $.ajax({
                    type: "GET",
                    url: "/" + this.value +"/alive",
                    success: function (msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank'>"
                            + msg
                            + "</a></div>");
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    }
    );

function goToHome() { $("#idContenido").load("index.html #idContenido"); return true; }
function goToCsv() { $("#idContenido").load("csv.html #idContenido"); return true; }
function goToAlive() { $("#idContenido").load("alive.html #idContenido"); return true; }


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
            }
        })
            .done(function (res) {
                //$("#mensaje").html("Respuesta: " + res);
                $("#myModal").modal();
                console.log("FICHERO SUBIDO");
                console.log(res);
                getCsvStatus(res);
            });
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
            console.log(res);
            console.log("ACABADO");
            if(res.uriResult != null){ // Conversion finished
                var win = window.open(res.uriResult, '_blank');
                win.focus();
            } else {
                setTimeout(getCsvStatus(url), 5000);
            }

        });
}


// For serialazing checkboxes
(function ($) {

    $.fn.serialize = function (options) {
        return $.param(this.serializeArray(options));
    };

    $.fn.serializeArray = function (options) {
        var o = $.extend({
            checkboxesAsBools: false
        }, options || {});

        var rselectTextarea = /select|textarea/i;
        var rinput = /text|hidden|password|search/i;

        return this.map(function () {
            return this.elements ? $.makeArray(this.elements) : this;
        })
            .filter(function () {
                return this.name && !this.disabled &&
                    (this.checked
                        || (o.checkboxesAsBools && this.type === 'checkbox')
                        || rselectTextarea.test(this.nodeName)
                        || rinput.test(this.type));
            })
            .map(function (i, elem) {
                var val = $(this).val();
                return val == null ?
                    null :
                    $.isArray(val) ?
                        $.map(val, function (val, i) {
                            return {name: elem.name, value: val};
                        }) :
                        {
                            name: elem.name,
                            value: (o.checkboxesAsBools && this.type === 'checkbox') ? //moar ternaries!
                                (this.checked ? 'true' : 'false') :
                                val
                        };
            }).get();
    };

})(jQuery);

function getJsonFromForm() {

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
