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


    private String user_id;
    private String user_room;

    @OnOpen
    public void onOpen(Session _session) {
        session = _session;
        id = _session.getId();
        logger.info(session.getId() + " Connected ... ");
        collection.put(id, this);
    }

    @OnMessage
    public String onMessage(String msg, Session _session)   {
        logger.info(session.getId() + " Received: " + msg);
        JSONTokener json_tokener = new JSONTokener(msg);
        JSONObject json_obj = (JSONObject) json_tokener.nextValue();
        logger.info("msg: " + json_obj.toString());

        Document doc = new Document("msg", json_obj.toString());
        MongoCollection<Document> collection = mongoDatabase.getCollection("log");
        collection.insertOne(doc);
        //session.getContainer();
        return msg;
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

    public String getId() {
        return id;
    }

    public void send(String string) {
        try {
            session.getBasicRemote().sendText(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendAll(String string) {
        for (HashMap.Entry<String, YouChatServerEndpoint> entry:
                collection.entrySet()) {
            entry.getValue().send(string);
        }
    }

}
