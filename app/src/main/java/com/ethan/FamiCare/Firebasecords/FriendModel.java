package com.ethan.FamiCare.Firebasecords;

public class FriendModel {
    private String profilepic;
    private String username;
    private String userId;
    private String id;
    private String token;
    private String type;
    private String fuid;


    public FriendModel() {

    }

    public FriendModel(String profilepic, String fuid, String id, String token,String type) {
        this.profilepic = profilepic;
        this.fuid=fuid;
        this.id = id;
        this.token = token;
        this.type=type;
    }

    public FriendModel(String profilepic, String username, String id, String token,String type,String fuid) {
        this.profilepic = profilepic;
        this.username = username;
        this.id = id;
        this.token = token;
        this.type=type;
        this.fuid=fuid;
    }
    public FriendModel(String username, String profilepic,String type) {
        this.username = username;
        this.profilepic = profilepic;
        this.type=type;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FriendModel(String username) {
        this.username = username;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }
}