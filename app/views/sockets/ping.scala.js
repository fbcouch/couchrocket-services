$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var dateSocket = new WS("ws://lvh.me:9000@routes.Socket.pingWs()")

    var receiveEvent = function(event) {
        $("#ping").html("Last ping: "+event.data);
    }

    dateSocket.onmessage = receiveEvent
})
