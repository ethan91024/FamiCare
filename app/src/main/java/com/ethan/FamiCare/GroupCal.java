package com.ethan.FamiCare;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//日記類別
@Entity(tableName = "groupcal")
public class GroupCal {
    @PrimaryKey//用日期當id
    private int id;
    @ColumnInfo(name = "Event")
    private String event;
    @ColumnInfo(name = "hour")
    private String hour;
    @ColumnInfo(name = "minute")
    private String minute;

    public GroupCal() {
    }

    public GroupCal(int id, String event, String hour,String minute) {
        this.id = id;
        this.event = event;
        this.hour = hour;
        this.minute=minute;
    }

    public int getId() {
        return id;
    }

    public void setId(int date) {
        this.id = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute(){return minute;}

    public void setMinute(String minute){this.minute=minute;}


    @Override
    public String toString() {
        return "GroupCal{" +
                "date = " + id +
                ", Event = " + event +
                ", hour = " + hour +
                ", minute = " + minute +
                '}';
    }
}
