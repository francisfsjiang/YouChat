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

youchat.handler = {};
youchat.handler["/msg"] = function() {
    var json = youchat.get_json(
        "msg",
        "",
        "",
        youchat.dom.input_text.val().split(" ").slice(1)
    );
    console.log("msg:" + json);
    youchat.client.ws.send(json);
};

youchat.handler["/login"] = function() {
    var json = youchat.get_json(
        "login",
        youchat.dom.input_text,
        "",
        youchat.dom.input_text.val().split(" ").slice(1)
    );
    console.log("login:" + json);
    youchat.client.ws.send(json);
};

youchat.handler["/reg"] = function() {

};

youchat.handler["/send"] = function() {

};

youchat.sha512 = function(str) {
    var shaObj = new jsSHA(str, "TEXT");
    return shaObj.getHash("SHA-512", "HEX");
};

youchat.get_json = function(type, id, room, msg) {
    return JSON.stringify({
        "type": type,
        "id": id,
        "room": room,
        "msg": msg
    });
};

youchat.keyup = function (e) {
    var key_code = e.which;
    switch (key_code) {
        case youchat.keymap.enter :
            youchat.send_cmd();
            break;
    }
};

youchat.send_cmd = function() {
    var cmd = youchat.dom.input_text.val();
    var type = "/msg";
    for (var i in youchat.handler) {
        if (cmd.search(i) == 0) {
            type = i;
        }
    }
    console.log("get " + type);
    youchat.handler[type]();

    youchat.dom.input_text.val("");
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