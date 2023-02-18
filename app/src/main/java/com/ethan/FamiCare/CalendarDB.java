package com.ethan.FamiCare;

public class CalendarDB {
   private String id;
   private String event;
   private String time;
   private String user;

    //for firebase getting data back
    public CalendarDB() {

    }

    public CalendarDB(String id,String event,String time,String user){
        this.id=id;
        this.event=event;
        this.time=time;
        this.user=user;
    }

    public String getId(){return id;}

    public String getEvent(){return event;}

    public String getTime(){return time;}

    public String getUser(){return user;}

}
