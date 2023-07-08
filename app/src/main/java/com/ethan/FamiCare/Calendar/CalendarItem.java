package com.ethan.FamiCare.Calendar;

public class CalendarItem {
    private String date_id;//日期
    private String email;
    private String event;
    private String time;
    private String who_recevice;
    private int image;

    public CalendarItem(){

    }

    public CalendarItem(String email,String event,String time,String who_recevice,int image){
        this.email=email;
        this.event=event;
        this.time=time;
        this.who_recevice=who_recevice;
        this.image=image;
    }
    public CalendarItem(String email,String event,String time){
        this.email=email;
        this.event=event;
        this.time=time;
    }
    public CalendarItem(String email,String event,String time,String who_recevice){
        this.email=email;
        this.event=event;
        this.time=time;
        this.who_recevice=who_recevice;
    }
    public CalendarItem(String email,String event){
        this.email=email;
        this.event=event;
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

    public int getImage() {
        return image;
    }

    public  void setImage(int image1){
        this.image=image1;
    }

    public String getWho_recevice(){
        return who_recevice;
    }
}
