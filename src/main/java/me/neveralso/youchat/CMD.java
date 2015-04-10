package me.neveralso.youchat;

public class CMD {
    public String type;
    public String id;
    public String room;
    public String msg;

    CMD(String _type, String _id, String _room, String _msg) {
        type = _type;
        id = _id;
        room = _room;
        msg = _msg;
    }
}
