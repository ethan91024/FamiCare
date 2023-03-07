package com.ethan.FamiCare;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//日記類別
@Entity(tableName = "diaries")
public class Diary {
    @PrimaryKey//用日期當id
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "photo_path")
    private String photoPath; // 新增的照片路徑

    public Diary() {
    }

    public Diary(int id, String title, String content, String photoPath) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.photoPath = photoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int date) {
        this.id = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return "Diary{" +
                "date = " + id +
                ", title = " + title +
                ", content = " + content +
                ", photoPath = " + photoPath +
                '}';
    }
}