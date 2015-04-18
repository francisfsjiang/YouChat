package me.neveralso.youchat;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.websocket.*;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;
import org.json.JSONTokener;

@ServerEndpoint(value = "/youchat")
public class YouChatServerEndpoint {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private DataBaseConn dbc = new DataBaseConn(logger);

    private Session session;
    private String id;

    private static HashMap<String, YouChatServerEndpoint> collection =
            new HashMap<>();

    private String userId;
    private String userRoom;

    @OnOpen
    public void onOpen(Session _session) {
        session = _session;
        id = _session.getId();
        logger.info(id + " Connected ... ");
        collection.put(id, this);
    }

    @OnMessage
    public void onMessage(String msg, Session _session) {
        logger.info(id + " Received: " + msg);

        parseCmd(msg);
    }

    @OnClose
    public void onClose(Session _session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
        collection.remove(id);

    }

    @OnError
    public void onError(Session _session, Throwable thr) {
        logger.info(String.format("Session %s error because of %s", session.getId(), thr));
        onClose(session, new CloseReason(CloseCodes.NORMAL_CLOSURE, " error occurred"));
    }

    private String getId() {
        return id;
    }

    private void send(String string) {
        try {
            session.getBasicRemote().sendText(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendAll(String string) {
        for (HashMap.Entry<String, YouChatServerEndpoint> entry :
                collection.entrySet()) {
            entry.getValue().send(string);
        }
    }

    private void parseCmd(String content) {
        JSONTokener json_tokener = new JSONTokener(content);
        JSONObject json_obj = (JSONObject) json_tokener.nextValue();
        logger.info("msg: " + json_obj.toString());

        String type = json_obj.getString("type");

        boolean ret = false;
        switch (type) {
            case "msg":
                handleMsg(content);
                break;
            case "join":
                handleChangeRoom(content);
                break;
            case "reg":
                handleRegister(content);
                break;
            case "login":
                handleLogin(content);
                break;
        }
    }

    private void handleMsg(String content) {

    }

    private void handleChangeRoom(String content) {

    }

    private void handleRegister(String content) {
        JSONTokener json_tokener = new JSONTokener(content);
        JSONObject json_obj = (JSONObject) json_tokener.nextValue();
        String user_id = json_obj.getString("user_id");
        String passwd = json_obj.getString("passwd");
        String email = json_obj.getString("email");

        Boolean ret = dbc.Register(user_id, passwd, email);
        JSONObject result = new JSONObject()
                .append("type", "reg")
                .append("status", ret.toString());

        if (ret) {
            result.append("msg", "reg succeed, please login");
        }
        else {
            result.append("msg", "reg failed");
        }

        send(result.toString());
    }

    private void handleLogin(String content) {
        JSONTokener json_tokener = new JSONTokener(content);
        JSONObject json_obj = (JSONObject) json_tokener.nextValue();
        String user_id = json_obj.getString("user_id");
        String passwd = json_obj.getString("passwd");

        Boolean ret = dbc.Login(user_id, passwd);
        JSONObject result = new JSONObject()
                .append("type", "msg")
                .append("status", ret.toString());
        if (ret) {
            result.append("msg", "login succeed, welcome back, " + user_id);
            userId = user_id;
            change_notify();
        }
        else {
            result.append("msg", "login failed");
        }
        send(result.toString());
    }

    private void change_notify() {
        JSONObject json = new JSONObject()
                .append("type", "notify")
                .append("user_id", userRoom)
                .append("user_room", userRoom);
        send(json.toString());
    }
}
