package com.scappy.twlight.SystemModels;

public class SystemModelForPosts {

    String id,pId,text,pViews,type,video,image,reTweet,pComments,privacy,pTime,reId;

    public SystemModelForPosts() {
    }

    public SystemModelForPosts(String id, String pId, String text, String pViews, String type, String video, String image, String reTweet, String pComments, String privacy, String pTime, String reId) {
        this.id = id;
        this.pId = pId;
        this.text = text;
        this.pViews = pViews;
        this.type = type;
        this.video = video;
        this.image = image;
        this.reTweet = reTweet;
        this.pComments = pComments;
        this.privacy = privacy;
        this.pTime = pTime;
        this.reId = reId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getpViews() {
        return pViews;
    }

    public void setpViews(String pViews) {
        this.pViews = pViews;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReTweet() {
        return reTweet;
    }

    public void setReTweet(String reTweet) {
        this.reTweet = reTweet;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getReId() {
        return reId;
    }

    public void setReId(String reId) {
        this.reId = reId;
    }
}
