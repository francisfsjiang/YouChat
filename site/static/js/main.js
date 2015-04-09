window.youchat = {};

youchat.keymap = {};
youchat.keymap.enter = 13;

youchat.dom = {};

youchat.user = {};
youchat.user.id = "Anonymous";
youchat.user.room = "Global";

youchat.client = {};
youchat.client.ws = null;
youchat.client.url = "ws://127.0.0.1:8025/api/youchat";

youchat.client.init = function () {
    youchat.client.ws = new WebSocket(youchat.client.url);
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
    var json = JSON.parse(e.data);
    console.log(json.msg);
    youchat.add_chat_content(json.msg);
    console.log("ws received: " + e.data);
};

youchat.client.callback.onclose = function (e) {
    console.log("ws closed");
};

youchat.client.callback.onerror = function (e) {
    console.log("ws error");
};

youchat.keyup = function (e) {
    var key_code = e.which;
    switch (key_code) {
        case youchat.keymap.enter :
            var json = JSON.stringify({
                "type": "msg",
                "id":youchat.user.id,
                "room":youchat.user.room,
                "msg": youchat.dom.input_text.val()
            });
            console.log("send:" + json);
            youchat.client.ws.send(json);
            youchat.dom.input_text.val("");

            break;
    }
};

youchat.update_chat_info = function() {
    youchat.dom.chat_info.html(youchat.user.id + "@" + youchat.user.room);
};

youchat.add_chat_content = function(str) {
    youchat.dom.chat_container.append($("<div>" + str + "</div>"));
};

youchat.init = function() {
    document.body.style.overflow = "hidden";
    //window.onbeforeunload = check_leave;
    function check_leave() {
        return "Don't leave, please. QAQ";
    }
    window.PerfectScrollbar.initialize(document.getElementById('chat-container'));
    youchat.dom.input_text = $("#input-text");
    youchat.dom.input_text.keyup(youchat.keyup);

    youchat.dom.chat_info = $("#input-info");
    youchat.update_chat_info();

    youchat.dom.chat_container = $("#chat-container:first-child");

    youchat.client.init();
};


$(document).ready(function() {
    console.log("init");
    youchat.init();
});