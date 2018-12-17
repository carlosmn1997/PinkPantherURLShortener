function goToHome() {
    $("#idContenido").load("index.html #idContenido", function(){
        $(document).ready($.getScript('js/index.js'));
    });
}
function goToCsv() {
    $("#idContenido").load("csv.html #idContenido", function(){
        $(document).ready($.getScript('js/csv.js'));
    });
}

function goToAlive() {
    $("#idContenido").load("alive.html #idContenido", function(){
        $(document).ready($.getScript('js/alive.js'));
    });
}