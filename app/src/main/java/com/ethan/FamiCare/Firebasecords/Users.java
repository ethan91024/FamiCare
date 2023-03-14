package com.ethan.FamiCare.Firebasecords;

public class Users {
    private String profilepic;
    private String userEmail;
    private String message;
    private String userId;
    private String username;
    private String status;
    private String password;
    public Users() {

    }

    public Users(String profilepic, String userEmail, String message, String userId, String username, String status, String password) {
        this.profilepic = profilepic;
        this.userEmail = userEmail;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.password = password;
    }

    public Users(String username,String userEmail,  String password) {
        this.userEmail = userEmail;
        this.password = password;
        this.username= username;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
