package com.ethan.FamiCare.Firebasecords;

public class SettingModel {

    private String userId;
    private String username;
    public SettingModel() {

    }

    public SettingModel(String userId, String username) {
        this.userId = userId;
        this.username = username;
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
}
