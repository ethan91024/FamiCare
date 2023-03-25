package com.ethan.FamiCare.Firebasecords;

public class GroupMessage {

    private String message;
    private Long datetime;
    private String userId;

    private String messageId;

    //for firebase getting data back
    public GroupMessage() {

    }

    public GroupMessage(String message, Long datetime, String userId) {
        this.message = message;
        this.datetime = datetime;
        this.userId = userId;
    }

    public GroupMessage(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}