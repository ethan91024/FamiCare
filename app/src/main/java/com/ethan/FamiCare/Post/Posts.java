package com.ethan.FamiCare.Post;

import java.util.ArrayList;
import java.util.List;

public class Posts {
    private int Id;
    private String Title;
    private String Content;
    private String photoUrl;
    private List<Comment> comments;

    public Posts() {
    }

    public Posts(int id, String title, String content, String photoUrl) {
        this.Id = id;
        this.Title = title;
        this.Content = content;
        this.photoUrl = photoUrl;
        this.comments = new ArrayList<>();
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
