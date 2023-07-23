package com.ethan.FamiCare.Firebasecords;

public class PermissionModel {
    Boolean step;
    Boolean heartrate;
    Boolean speed;
    Boolean calories;
    Boolean breathe;
    Boolean oxygen;
    Boolean sleep;

   public PermissionModel(){
    }

   public PermissionModel(Boolean step,Boolean heartrate,Boolean speed,Boolean calories,Boolean breathe,Boolean oxygen,Boolean sleep){
       this.step=step;
       this.heartrate=heartrate;
       this.speed=speed;
       this.calories=calories;
       this.breathe=breathe;
       this.oxygen=oxygen;
       this.sleep=sleep;
   }
   public Boolean getStep(){
       return step;
   }
   public void setStep(Boolean step){
       this.step=step;
   }
    public Boolean getHeartrate(){
        return heartrate;
    }
    public void setHeartrate(Boolean heartrate){
        this.heartrate=heartrate;
    }
    public Boolean getSpeed(){
        return speed;
    }
    public void setSpeed(Boolean speed){
        this.speed=speed;
    }
    public Boolean getCalories(){
        return calories;
    }
    public void setCalories(Boolean calories){
        this.calories=calories;
    }
    public Boolean getBreathe(){
        return breathe;
    }
    public void setBreathe(Boolean breathe){
        this.breathe=breathe;
    }
    public Boolean getOxygen(){
        return oxygen;
    }
    public void setOxygen(Boolean oxygen){
        this.oxygen=oxygen;
    }
    public Boolean getSleep(){
        return sleep;
    }
    public void setSleep(Boolean sleep){
        this.sleep=sleep;
    }




}
