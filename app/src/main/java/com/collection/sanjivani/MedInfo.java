package com.collection.sanjivani;

public class MedInfo {
    public String medName, medAvailability, medPrice;

    public MedInfo(){

    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getMedAvailability() {
        return medAvailability;
    }

    public void setMedAvailability(String medAvailability) {
        this.medAvailability = medAvailability;
    }

    public String getMedPrice() {
        return medPrice;
    }

    public void setMedPrice(String medPrice) {
        this.medPrice = medPrice;
    }

    public MedInfo(String medName, String medAvailability, String medPrice) {
        this.medName = medName;
        this.medAvailability = medAvailability;
        this.medPrice = medPrice;
    }
}
