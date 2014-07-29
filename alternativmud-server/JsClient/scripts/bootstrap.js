function loadCssFile(path) {
    /*$.ajax({
        url: path+"?_uid="+(new Date()).getTime(),
        async: false
    }).done(function(data) { 
        $('head').append("<"+"style type=\"text/css\""+">"+data+"<"+"/style"+">");
        console.log("Loaded css file: "+path);
    }).fail(function() {
        $('#bootstrap-msg').css("color", "red").html("Sorry, could not load css file: "+path);
    });*/
    $('head').append("<"+"link rel=\"stylesheet\" type=\"text/css\" href=\""+(path+"?_uid="+(new Date()).getTime())+"\"/>");
}

function loadJsFile(path) {
    $.ajax({
        url: path+"?_uid="+(new Date()).getTime(),
        dataType: "script",
        async: false
    }).done(function(data) { 
        //$('head').append("<"+"script type=\"text/javascript\""+">"+data+"<"+"/script"+">");
        console.log("Evaluated script "+path);
    }).fail(function() {
        $('#bootstrap-msg').css("color", "red").html("Sorry, could not load script: "+path);
        throw "Could not load script: "+path;
    });
}

var imagesToPreload = new Array();
function preloadImage(path) {
    imagesToPreload.push(path);
}

function preConfiguration() {
    $("body").append("<div id=\"image-preloader-div\">&nbsp;</div>");
    $("#image-preloader-div").hide();
    
    preloadImage("img/loading.gif");
}

function hideLoader() {
    $('.bootstrap-loader').hide();
}

$(document).ready(function() {
    preConfiguration();
    
    loadJsFile("lib/js/jquery.json-2.3.min.js");
    loadJsFile("lib/js/less-1.3.0.min.js");
    loadJsFile("lib/js/raphael-min.js");
    loadJsFile("fonts/FnT_BasicShapes1.js");
    loadJsFile("fonts/FreeTiles.js");
    loadJsFile("scripts/util.js");
    loadJsFile("scripts/config.js");
    loadJsFile("scripts/ui.js");
    loadJsFile("scripts/communication.js");
    loadJsFile("scripts/main.js");
    loadJsFile("scripts/indicators.js");
    
    $('#bootstrap-msg').css("color", "white").html("Loading graphics... 0%");
    var num = 0;
    $.preload(imagesToPreload, {
            loaded: function(loaded, total) {
                num++;
                $('#bootstrap-msg').css("color", "white").html("Loading graphics... "+Math.round(num/total*100)+"%");
                console.log("Preloaded image "+num+"/"+total+".");
            },
            loaded_all: function(loaded, total) {
                console.log("Finished loading images.");
                
                getUI().construct();
                hideLoader();
                getUI().activate();
                
                connect();
            }
        });
});