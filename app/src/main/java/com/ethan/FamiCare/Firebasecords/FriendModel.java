package com.ethan.FamiCare.Firebasecords;

public class FriendModel {
    private String profilepic;
    private String username;
    private String userId;
    private String id;

    public FriendModel() {

    }

    public FriendModel(String username, String profilepic, String userId, String id) {
        this.username = username;
        this.profilepic = profilepic;
        this.userId = userId;
        this.id = id;
    }

    public FriendModel(String username, String profilepic, String id) {
        this.username = username;
        this.profilepic = profilepic;
        this.id=id;
    }
    public FriendModel(String username, String profilepic) {
        this.username = username;
        this.profilepic = profilepic;
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