package com.ethan.FamiCare.Firebasecords;

public class SymptomModel {
    boolean headache;
    boolean dizzy;
    boolean nausea;
    boolean tired;
    boolean stomachache;
    double symptomN;


    public SymptomModel(  boolean headache,boolean dizzy,boolean nausea,boolean tired,boolean stomachache,double symptomN){
        this.headache=headache;
        this.dizzy=dizzy;
        this.nausea =nausea;
        this.tired=tired;
        this.stomachache=stomachache;
        this.symptomN=symptomN;

    }




    public double getSymptomN() {
        return symptomN;
    }

    public void setSymptomN(double symptomN) {
        this.symptomN = symptomN;
    }

    public boolean isHeadache() {
        return headache;
    }

    public void setHeadache(boolean headache) {
        this.headache = headache;
    }

    public boolean isDizzy() {
        return dizzy;
    }

    public void setDizzy(boolean dizzy) {
        this.dizzy = dizzy;
    }

    public boolean isNausea() {
        return nausea;
    }

    public void setNausea(boolean nausea) {
        this.nausea = nausea;
    }

    public double getPressn() {
        return symptomN;
    }

    public void setPressn(double symptomN) {
        this.symptomN = symptomN;
    }

    public boolean isTired() {
        return tired;
    }

    public void setTired(boolean tired) {
        this.tired = tired;
    }

    public boolean isStomachache() {
        return stomachache;
    }

    public void setStomachache(boolean stomachache) {
        this.stomachache = stomachache;
    }
}

