function goToHome() {
    $("#idContenido").load("index.html #idContenido");
    $("#shortener").on('submit');
    $.getScript('js/index.js');
    return false;
}
function goToCsv() {
    $("#idContenido").load("csv.html #idContenido");
    $("#uploadCsv").on('submit');
    $.getScript('js/csv.js');
    return false;
}

function goToAlive() {
    $("#idContenido").load("alive.html #idContenido");
    $("#alive").on('submit');
    $.getScript('js/alive.js');
    return false;
}