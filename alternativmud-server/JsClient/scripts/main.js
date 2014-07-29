var ws;
var user = null;
var characters = null;
var character;

var hpIndicator, mvIndicator;

$(document).ready(function () {
    ws = new WebSocket("ws://carewna.jblew.pl:8887/websocket"); 
    ws.onopen = function(event) {
        $("#intro-menu-msg").removeClass().addClass("alert alert-info");
        $('#intro-menu-msg').html("You have to log in.");
        $("#login-menu").show();
    }
    ws.onmessage = function(onMessageEvent) {
        //$("#intro-menu-msg").removeClass().addClass("alert alert-info");
        //$("#intro-menu-msg").html($("#intro-menu-msg").html()+"<br />"+event.data);
        //$('#result').html($('#result').html()+"<br />"+event.data); 
        var event = jQuery.parseJSON(onMessageEvent.data);
        //alert(dump(event));
        if(event.className == "net.alternativmud.system.nebus.server.StandardBusSubscriber$LoginSucceeded") {
            user = event.object.user;
            $("#intro-menu-msg").removeClass().addClass("alert alert-success").html("Hi, "+event.object.user.login+"!");
            $("#characters-menu").show();
            $("#login-menu").hide();
            ws.send($.toJSON({
                className: "net.alternativmud.system.nebus.server.AuthenticatedBusSubscriber$GetCharacters",
                object: {}
            }));
        }
        else if(event.className == "net.alternativmud.system.nebus.server.StandardBusSubscriber$LoginFailed") {
            $("#intro-menu-msg").removeClass().addClass("alert alert-error").html("Cannot log in. Bad login or password.");
        }
        else if(event.className == "net.alternativmud.system.nebus.server.AuthenticatedBusSubscriber$Characters") {
            var charactersTable = "<table class=\"table table-bordered table-bg\">"
            +"<tr><th>#</th><th>Name</th><th>Actions</th></tr>";
            var ci;
            var num = 1;
            characters = event.object.characters;
            for(ci in event.object.characters) {
                var character = event.object.characters[ci];
                charactersTable += "<tr><td>"+num+"</td><td>"+character.name+"</td>"
                +"<td><a href=\"#\" class=\"play-character\" data-characterid=\""+ci+"\">Play</a></td></tr>";
                num++;
            }
            charactersTable += "</table>";
            $("#characters-menu").html(charactersTable);
            $(".play-character").click(function(evt) {
                character = characters[$(evt.target).attr('data-characterid')];
                $("#intro-menu-msg").removeClass().addClass("alert alert-info").html("Requesting new gameplay...");
                ws.send($.toJSON({
                    className: "net.alternativmud.system.nebus.server.AuthenticatedBusSubscriber$StartGameplay",
                    object: {
                        characterName: character.name
                    }
                }));
            });
        }
        else if(event.className == "net.alternativmud.system.nebus.server.AuthenticatedBusSubscriber$GameplayStarted") {
            $("#main-sphere-menu-overlay").hide();
            startGame();
        }
        else if(event.className == "net.alternativmud.system.nebus.server.AuthenticatedBusSubscriber$GameplayStartFailed") {
            $("#intro-menu-msg").removeClass().addClass("alert alert-error").html("Could not start new gameplay: "+event.object.message);
        }
        else if(event.className == "net.alternativmud.logic.event.SendTextToUser") {
            $(".console").html($(".console").html()+"<span class=\"x\">"
                +event.object.text
                .replace("\n", "<br />")
                .replace("\r", "")
                .replace("/{v/g", "</span><span class=\"v\">")
                .replace("/{V/g", "</span><span class=\"vv\">")
                .replace("/{r/g", "</span><span class=\"r\">")
                .replace("/{R/g", "</span><span class=\"rr\">")
                .replace("/{g/g", "</span><span class=\"g\">")
                .replace("/{G/g", "</span><span class=\"gg\">")
                .replace("/{y/g", "</span><span class=\"y\">")
                .replace("/{Y/g", "</span><span class=\"yy\">")
                .replace("/{b/g", "</span><span class=\"b\">")
                .replace("/{B/g", "</span><span class=\"bb\">")
                .replace("/{m/g", "</span><span class=\"m\">")
                .replace("/{M/g", "</span><span class=\"mm\">")
                .replace("/{c/g", "</span><span class=\"c\">")
                .replace("/{C/g", "</span><span class=\"cc\">")
                .replace("/{x/g", "</span><span class=\"x\">")
                .replace("/{X/g", "</span><span class=\"xx\">")
                +"</span>");
        }
        else {
            //alert(event+"; RAW: "+onMessageEvent.data);
            //alert(dump(event));
        } 
    }
    ws.onclose = function(event) {
        $("#intro-menu-msg").removeClass().addClass("alert alert-error");
        $('#intro-menu-msg').html("Connection has been closed.");
    }
                
    $("#login-btn").click(function(e) {
        e.preventDefault();
        ws.send($.toJSON({
            className: "net.alternativmud.system.nebus.server.StandardBusSubscriber$PerformLogin",
            object: {
                login: $('#login-field').val(),
                password: $('#password-field').val()
            }
        }));
    });
}); 
            
function startGame() {
    ws.send($.toJSON({
        className: "net.alternativmud.logic.event.GameplayClientReady",
        object: {}
    }));
    sendCommand("n");
    sendCommand("look");
    
    $(".console-overlay").show();
    hpIndicator = createIndicator(80, 110, 40, "#A62300", 10);
    hpIndicator.updateVal(100);
    hpIndicator.raphael.print(24, 48, "O", hpIndicator.raphael.getFont("FnT_BasicShapes1"), 60).attr({
        fill: "#FF9073"
    });
    
    mvIndicator = createIndicator(200, 90, 40, "#015666", 10);
    mvIndicator.updateVal(80);
    mvIndicator.raphael.print(31, 48, "T", mvIndicator.raphael.getFont("FnT_BasicShapes1"), 60).attr({
        fill: "#5FBDCE"
    });
}
         
function sendCommand(text){
    ws.send('{"className": "net.alternativmud.logic.event.ReceivedTextFromUser",'
        +'"object": {"text": "'+text+'"}}'); 
}
