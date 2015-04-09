package me.neveralso.youchat;

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


    @OnOpen
    public void onOpen(Session session) {
        logger.info(session.getId() + " Connected ... ");

        SessionContainer.addSession(session);
    }

    @OnMessage
    public String onMessage(String msg, Session session)   {
        logger.info(session.getId() + " Received: " + msg);
        JSONTokener json_tokener = new JSONTokener(msg);
        JSONObject json_obj = (JSONObject) json_tokener.nextValue();
        logger.info("type: " + json_obj.getString("type"));
        logger.info("msg: " + json_obj.getString("msg"));
        Document doc = new Document("type", json_obj.getString("type"))
                .append("msg", json_obj.getString("msg"));
        MongoCollection<Document> collection = mongoDatabase.getCollection("log");
        collection.insertOne(doc);
        return msg;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
        SessionContainer.removeSession(session.getId());
        mongoClient.close();
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        logger.info(String.format("Session %s error because of %s", session.getId(), thr));
        onClose(session, new CloseReason(CloseCodes.NORMAL_CLOSURE, " error occurred"));
    }


}
