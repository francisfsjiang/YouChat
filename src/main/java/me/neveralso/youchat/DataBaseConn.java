package me.neveralso.youchat;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.mongodb.MongoClient;
import org.bson.Document;

public class DataBaseConn {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public DataBaseConn(){
        mongoClient = new MongoClient("localhost", 27017);
        mongoDatabase = mongoClient.getDatabase("youchat");
    }

//    JSONTokener json_tokener = new JSONTokener(msg);
//    JSONObject json_obj = (JSONObject) json_tokener.nextValue();
//    logger.info("msg: " + json_obj.toString());
//
//    CMD cmd = new CMD(
//            json_obj.getString("type"),
//            json_obj.getString("id"),
//            json_obj.getString("room"),
//            json_obj.getString("msg")
//    );

    public void Register() {
        Document doc = new Document("user",cmd.id);
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");
        collection.insertOne(doc);
    }

    public void Close() {
        mongoClient.close();
    }
}
