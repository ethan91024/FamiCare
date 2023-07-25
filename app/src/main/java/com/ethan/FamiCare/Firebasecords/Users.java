package com.ethan.FamiCare.Firebasecords;

public class Users {
    private String profilepic;
    private String userEmail;
    private String message;
    private String userId;
    private String username;
    private String status;
    private String password;
    private String token;
    private String id;
    private String fuid;
    private String groupuid;

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

    public Users(String username, String userEmail, String password, String token, String id) {
        this.userEmail = userEmail;
        this.password = password;
        this.username = username;
        this.token = token;
        this.id = id;
    }

    public Users(String profilepic, String username, String userEmail, String password, String token, String id) {
        this.profilepic = profilepic;
        this.userEmail = userEmail;
        this.password = password;
        this.username = username;
        this.token = token;
        this.id = id;
    }

    public String getGroupuid() {
        return groupuid;
    }

    public void setGroupuid(String groupuid) {
        this.groupuid = groupuid;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
