package me.neveralso.websocket;

import java.io.IOException;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;
import org.json.JSONTokener;

@ServerEndpoint(value = "/game")
public class WordgameServerEndpoint {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @OnOpen
    public void onOpen(Session session) {
        logger.info(session.getId() + " Connected ... ");
    }

    @OnMessage
    public String onMessage(String msg, Session session)   {
        logger.info(session.getId() + " Received: " + msg);
        JSONTokener json_tokener = new JSONTokener(msg);
        JSONObject json_obj = (JSONObject) json_tokener.nextValue();
        logger.info("type: " + json_obj.getString("type"));
        logger.info("msg: " + json_obj.getString("msg"));
        return msg;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }


}
