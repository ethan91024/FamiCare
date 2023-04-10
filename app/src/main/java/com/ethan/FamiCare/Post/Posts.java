package com.ethan.FamiCare.Post;

public class Posts {
    private int Id;
    private String Title;
    private String Content;
    public String photoUrl;

    public Posts() {
    }

    public Posts(int id, String title, String content, String photoUrl) {
        this.Id = id;
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
