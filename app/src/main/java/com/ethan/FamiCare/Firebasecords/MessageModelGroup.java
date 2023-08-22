package com.ethan.FamiCare.Firebasecords;

public class MessageModelGroup {

    private String message;
    private Long datetime;
    private String userId;
    private String messageId;
    private String username;
    private String profileimage;

    //for firebase getting data back
    public MessageModelGroup() {

    }




    public MessageModelGroup(String username,String userId,String message, Long datetime,String profileimage) {
        this.username=username;
        this.message = message;
        this.datetime = datetime;
        this.userId = userId;
        this.profileimage=profileimage;
    }
    public MessageModelGroup(String username,String userId,String message) {
        this.username=username;
        this.message = message;
        this.userId = userId;
    }
    public MessageModelGroup(String userId,String message) {
        this.message = message;
        this.userId = userId;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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


