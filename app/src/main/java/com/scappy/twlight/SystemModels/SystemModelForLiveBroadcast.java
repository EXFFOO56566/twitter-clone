package com.scappy.twlight.SystemModels;

public class SystemModelForLiveBroadcast {
    String room,userid;

    public SystemModelForLiveBroadcast() {
    }

    public SystemModelForLiveBroadcast(String room, String userid) {
        this.room = room;
        this.userid = userid;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
