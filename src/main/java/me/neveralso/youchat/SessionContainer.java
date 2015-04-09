package me.neveralso.youchat;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class SessionContainer {
    private static HashMap<String, Session> map =
            new HashMap<>();

    public static void addSession(Session session) {
        map.put(session.getId(), session);
    }

    public static Session getSession(String id) {
        return map.get(id);
    }

    public static void removeSession(String id) {
        map.remove(id);
    }

}
