package com.scappy.twlight.SystemModels;

public class SystemModelForComments {
    String cId,comment,id,timestamp,pId,type,reTweet,reId;

    public SystemModelForComments() {
    }

    public SystemModelForComments(String cId, String comment, String id, String timestamp, String pId, String type, String reTweet, String reId) {
        this.cId = cId;
        this.comment = comment;
        this.id = id;
        this.timestamp = timestamp;
        this.pId = pId;
        this.type = type;
        this.reTweet = reTweet;
        this.reId = reId;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReTweet() {
        return reTweet;
    }

    public void setReTweet(String reTweet) {
        this.reTweet = reTweet;
    }

    public String getReId() {
        return reId;
    }

    public void setReId(String reId) {
        this.reId = reId;
    }
}
