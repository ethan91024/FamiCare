package com.ethan.FamiCare.Post;

public class Posts {
    private int Id;
    private String Title;
    private String Content;
//    public String PhotoUrl;

    public Posts() {
    }

    public Posts(int id, String title, String content) {
        this.Id = id;
        this.Title = title;
        this.Content = content;
//        this.PhotoUrl = photoUrl;
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

//    public String getPhotoUrl() {
//        return PhotoUrl;
//    }
//
//    public void setPhotoUrl(String photoUrl) {
//        this.PhotoUrl = photoUrl;
//    }

}
