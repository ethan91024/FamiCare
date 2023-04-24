package com.ethan.FamiCare.Calendar;

public class CalendarItem {
    private String date_id;
    private String email;
    private String event;
    private String time;

    private int image;

    public CalendarItem(String date_id,String email,String event,String time){
        this.date_id=date_id;
        this.email=email;
        this.event=event;
        this.time=time;
    }

    public String getDate_id(){
        return date_id;

    }

    public String getEmail(){
        return email;
    }

    public String getEvent(){
        return event;
    }
    public String getTime(){
        return time;

    }
}
