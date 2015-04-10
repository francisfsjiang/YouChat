package me.neveralso.youchat;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.websocket.*;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.mongodb.MongoClient;
import org.bson.Document;

@ServerEndpoint(value = "/youchat")
public class YouChatServerEndpoint {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private MongoClient mongoClient = new MongoClient("localhost", 27017);
    private MongoDatabase mongoDatabase = mongoClient.getDatabase("youchat");

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
        JSONTokener json_tokener = new JSONTokener(msg);
        JSONObject json_obj = (JSONObject) json_tokener.nextValue();
        logger.info("msg: " + json_obj.toString());

        CMD cmd = new CMD(
                json_obj.getString("type"),
                json_obj.getString("id"),
                json_obj.getString("room"),
                json_obj.getString("msg")
        );

        parseCmd(cmd);
    }

    @OnClose
    public void onClose(Session _session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
        collection.remove(id);
        mongoClient.close();
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

    private void parseCmd(CMD cmd) {
        boolean ret = false;
        switch (cmd.type) {
            case "msg":
                ret = handleMsg(cmd);
                break;
            case "join":
                ret = handleChangeRoom(cmd);
                break;
            case "register":
                ret = handleRegister(cmd);
                break;
            case "login":
                ret = handleLogin(cmd);
                break;
        }

        JSONObject json = new JSONObject().
                append("status", ret);

        send(json.toString());
    }

    private boolean handleMsg(CMD cmd) {
        return false;
    }

    private boolean handleChangeRoom(CMD cmd) {
        return false;
    }

    private boolean handleRegister(CMD cmd) {
        Document doc = new Document("user",cmd.id);
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");
        collection.insertOne(doc);
        return true;
    }

    private boolean handleLogin(CMD cmd) {
        return false;
    }
}
