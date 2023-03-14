package com.ethan.FamiCare.Firebasecords;

public class GroupMessage {
    private String userEmail;
    private String message;
    private String datetime;
    private String userId;
    private String profilepic;
    private String username;
    private String status;
    private String password;
    //for firebase getting data back
    public GroupMessage() {

    }

    public GroupMessage(String userEmail, String message, String datetime, String userId, String profilepic, String username, String status,String password) {
        this.userEmail = userEmail;
        this.message = message;
        this.datetime = datetime;
        this.userId = userId;
        this.profilepic = profilepic;
        this.username = username;
        this.status = status;
        this.password=password;
    }

    public GroupMessage(String userEmail, String message, String datetime) {
        this.userEmail = userEmail;
        this.message = message;
        this.datetime = datetime;
    }
//1

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

