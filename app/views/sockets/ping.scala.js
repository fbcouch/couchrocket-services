@(username: String)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var dateSocket = new WS("ws://" + location.host + "@routes.Socket.ws(username)")

    var receiveEvent = function(event) {
        console.log('event', event);
        $("#ping").html("Last ping: "+event.data);
    }

    dateSocket.onmessage = receiveEvent
})
