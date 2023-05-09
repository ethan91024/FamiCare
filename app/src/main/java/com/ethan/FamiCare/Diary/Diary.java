package com.ethan.FamiCare.Diary;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

//日記類別
@Entity(tableName = "diaries", primaryKeys = {"id", "title"})
public class Diary {
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    @NonNull
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "photo_path")
    private String photoPath;


    @ColumnInfo(name = "isSaved")
    private boolean isSaved = false;

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

    public void setId(int id) {
        this.id = id;
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

    public boolean getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean b) {
        this.isSaved = b;
    }


    @Override
    public String toString() {
        return "Diary{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", isSaved='" + isSaved + '\'' +
                '}';
    }
}