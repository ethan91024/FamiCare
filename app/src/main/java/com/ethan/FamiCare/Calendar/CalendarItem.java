package com.ethan.FamiCare.Calendar;

public class CalendarItem {
    private String date_id;//日期
    private String email;
    private String event;
    private String time;

    private int image;

    public CalendarItem(){

    }

    public CalendarItem(String date_id,String email,String event,String time){
        this.date_id=date_id;
        this.email=email;
        this.event=event;
        this.time=time;
    }

    public String getDate_id(){
        return date_id;

    }
    public void setDate_id(String date){
            this.date_id=date;
    }
    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public String getEvent(){
        return event;
    }

    public void setEvent(String event){
        this.event=event;
    }
    public String getTime(){
        return time;

    }
    public void setTime(String time){
        this.time=time;
    }

}
