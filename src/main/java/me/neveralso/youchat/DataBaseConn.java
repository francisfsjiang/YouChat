package me.neveralso.youchat;

import java.util.logging.Logger;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.mongodb.MongoClient;
import org.bson.Document;

public class DataBaseConn {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    MongoCollection<Document> userCollection;
    MongoCollection<Document> logCollection;
    MongoCollection<Document> msgCollection;

    private Logger logger;

    public DataBaseConn(Logger _logger){
        logger = _logger;
        init();
    }

    private void init() {
        mongoClient = new MongoClient("localhost", 27017);
        mongoDatabase = mongoClient.getDatabase("youchat");

        userCollection = mongoDatabase.getCollection("user");
        logCollection  = mongoDatabase.getCollection("log");
        msgCollection  = mongoDatabase.getCollection("msg");
    }

    public boolean Register(String user_id, String passwd, String email) {
        Document doc = new Document("user_id", user_id)
                .append("passwd", passwd)
                .append("email", email);
        try {
            userCollection.insertOne(doc);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean Login(String user_id, String passwd) {
        String r_passwd;
        try {
            Document doc = userCollection.find(eq("user_id", user_id)).limit(1).first();
            r_passwd = doc.getString("passwd");
        }
        catch (Exception e) {
            return false;
        }
        logger.info("varfiy :" + user_id);
        logger.info("input passwd:" + passwd);
        logger.info("right passwd:" + r_passwd);
        return passwd.equals(r_passwd);

    }

    public void Log(String log) {
        Document doc = new Document("content",log);
        logCollection.insertOne(doc);
    }

    public void Close() {
        mongoClient.close();
    }
}
