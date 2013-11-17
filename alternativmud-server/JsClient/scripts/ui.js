preloadImage("img/fire.png");
preloadImage("img/fire_logo.png");
loadCssFile("styles/ui.css");
loadJsFile("lib/jquery-ui/js/jquery-ui-1.8.18.custom.min.js");
loadCssFile("lib/jquery-ui/css/ui-nocte-irae/jquery-ui-1.8.18.custom.css");

var ui = new Object();

ui.construct = function() {
    $("body").append('<div class="fire-bg" id="fire-bg">&nbsp;</div>');
}

ui.activate = function() {
    
    }

ui.showDialog = function(title, cssClass, msg) {
    var elem = $('<div title="'+title+'"><p>'+msg+'</p></div>');
    elem.appendTo("body");
    elem.dialog({
        modal: true,
        buttons: {
            Ok: function() {
                $(this).dialog( "close" );
            }
        },
        dialogClass: cssClass
    });
}

ui.showWarningDialog = function(title, msg) {
    this.showDialog(title, "dialog-warning", msg);
}

ui.showInfoDialog = function(title, msg) {
    this.showDialog(title, "dialog-info", msg);
}

ui.showErrorDialog = function(title, msg) {
    this.showDialog(title, "dialog-error", msg);
}

function getUI() {
    return ui;
}