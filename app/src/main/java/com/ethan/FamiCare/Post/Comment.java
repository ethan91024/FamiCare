package com.ethan.FamiCare.Post;

public class Comment {
    private String Id;
    private String UserName;
    private String Content;

    public Comment() {
    }

    public Comment(String id, String userName, String content) {
        this.Id = id;
        this.UserName = userName;
        this.Content = content;
    }

    public String getId() {
        return Id;
    }

    public void setId(String userId) {
        this.Id = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }
}