var webSocket;

function connect() {
    webSocket = new WebSocket(getConfig().webSocketUrl); 
    webSocket.onopen = function() {
        getUI().showInfoDialog("Connection opened", "Opened new connection.");
    }
    
    webSocket.onclose = function(event) {
        
        $("#intro-menu-msg").removeClass().addClass("alert alert-error");
        $('#intro-menu-msg').html("Connection has been closed.");
    }
}

function getWebSocket() {
    return webSocket;
}