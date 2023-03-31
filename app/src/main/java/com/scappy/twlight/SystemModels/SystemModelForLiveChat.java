package com.scappy.twlight.SystemModels;

public class SystemModelForLiveChat {
    String ChatId,userId,msg;

    public SystemModelForLiveChat() {
    }

    public SystemModelForLiveChat(String chatId, String userId, String msg) {
        ChatId = chatId;
        this.userId = userId;
        this.msg = msg;
    }

    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String chatId) {
        ChatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
