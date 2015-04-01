window.youchat = {};

youchat.client = {};
youchat.client.ws = null;
youchat.client.host = "wx://127.0.0.1";
youchat.client.port = "8025";
youchat.client.path = "/websockets/game";

youchat.client.init = function () {
    youchat.client.ws = new WebSocket(
        youchat.client.host +
        " " +
        youchat.client.port +
        youchat.client.path
    );
    youchat.client.ws.onopen = youchat.client.callback.onopen;
    youchat.client.ws.onmessage = youchat.client.callback.onmessage;
    youchat.client.ws.onclose = youchat.client.callback.onclose;
    youchat.client.ws.onerror = youchat.client.callback.onerror;
};


youchat.client.callback = {};
youchat.client.callback.onopen = function () {
    console.log("ws open");
};

youchat.client.callback.onmessage = function (e) {
    console.log("ws received: " + e.data);
};

youchat.client.callback.onclose = function (e) {
    console.log("ws closed");
};

youchat.client.callback.onerror = function (e) {
    console.log("ws error");
};

youchat.init = function() {
    document.body.style.overflow = "hidden";
    //window.onbeforeunload = check_leave;
    function check_leave() {
        return "Don't leave, please. QAQ";
    }
    window.PerfectScrollbar.initialize(document.getElementById('chat-container'));
    //youchat.client.init();
};


$(document).ready(function() {
    youchat.init();
});