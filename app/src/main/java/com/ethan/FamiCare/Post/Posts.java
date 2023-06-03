package com.ethan.FamiCare.Post;

import java.util.ArrayList;
import java.util.List;

public class Posts {
    private int Id;
    private String UserName;
    private String Title;
    private String Content;
    private String photoUrl;

    public Posts() {
    }

    public Posts(int id, String userName, String title, String content, String photoUrl) {
        this.Id = id;
        this.UserName = userName;
        this.Title = title;
        this.Content = content;
        this.photoUrl = photoUrl;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public String getphotoUrl() {
        return photoUrl;
    }

    public void setphotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
