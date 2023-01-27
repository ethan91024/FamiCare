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

    public Diary() {
    }

    public Diary(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
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

    @Override
    public String toString() {
        return "Diary{" +
                "date = " + id +
                ", title = " + title +
                ", content = " + content +
                '}';
    }
}