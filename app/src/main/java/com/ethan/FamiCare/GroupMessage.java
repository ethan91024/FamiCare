package com.ethan.FamiCare;

public class GroupMessage {
    private String userEmail;
    private String message;
    private String datetime;
    private String userId;

    //for firebase getting data back
    public GroupMessage() {

    }
    //1
    public GroupMessage(String userEmail, String message, String datetime,String userId) {
        this.userEmail = userEmail;
        this.message = message;
        this.datetime = datetime;
        this.userId=userId;
    }
    public String getUserId() {
        return userId;
    }
    public void getUserId(String userId) {
        this.userId = userId;
    }
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

