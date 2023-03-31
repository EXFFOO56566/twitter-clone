package com.scappy.twlight.SystemModels;

public class SystemModelForChat {

    private String sender;
    private String receiver;
    private String msg;
    private String type;
    private String timeStamp;
    private boolean isSeen;

    public SystemModelForChat(String sender, String receiver, String msg, String type, boolean isSeen, String timeStamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.type = type;
        this.timeStamp = timeStamp;
        this.isSeen = isSeen;
    }

    public SystemModelForChat() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
