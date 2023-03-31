package com.scappy.twlight.SystemModels;

public class SystemModelForVerification {
    String id,type,gId,refOne,refTwo;

    public SystemModelForVerification() {
    }

    public SystemModelForVerification(String id, String type, String gId, String refOne, String refTwo) {
        this.id = id;
        this.type = type;
        this.gId = gId;
        this.refOne = refOne;
        this.refTwo = refTwo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getgId() {
        return gId;
    }

    public void setgId(String gId) {
        this.gId = gId;
    }

    public String getRefOne() {
        return refOne;
    }

    public void setRefOne(String refOne) {
        this.refOne = refOne;
    }

    public String getRefTwo() {
        return refTwo;
    }

    public void setRefTwo(String refTwo) {
        this.refTwo = refTwo;
    }
}
