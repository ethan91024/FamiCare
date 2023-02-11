package com.ethan.FamiCare;

public class CalendarDB {
   private String id;
   private String event;
   private String time;

    //for firebase getting data back
    public CalendarDB() {

    }

    public CalendarDB(String id,String event,String time){
        this.id=id;
        this.event=event;
        this.time=time;
    }

    public String getId(){return id;}

    public void setId(){this.id=id;}

    public String getEvent(){return event;}

    public void setEvent(){this.event=event;}

    public String getTime(){return time;}

    public void setTime(){this.time=time;}

}
