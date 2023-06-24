package com.ethan.FamiCare;

public class CalendarDB {
   private String id;
   private String event;
   private String time;
   private String user;
   private String token;

   private Boolean notiischoose;

    //for firebase getting data back
    public CalendarDB() {

    }

    public CalendarDB(String id,String event,String time,String user,String token,Boolean noti){
        this.id=id;
        this.event=event;
        this.time=time;
        this.user=user;
        this.token=token;
        this.notiischoose=noti;
    }

    public String getId(){return id;}

    public String getEvent(){return event;}

    public String getTime(){return time;}

    public String getUser(){return user;}

    public String getToken(){return token;}
    public Boolean getNotiischoose(){return notiischoose;}

}
