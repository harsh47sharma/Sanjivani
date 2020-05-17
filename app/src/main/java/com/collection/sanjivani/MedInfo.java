package com.collection.sanjivani;

import java.io.Serializable;

public class MedInfo implements Serializable {
    public String medName, medAvailability, medPrice;
    public String medDescription, medQuantity;

    public MedInfo(){

    }

    public MedInfo(String medName, String medAvailability, String medPrice) {
        this.medName = medName;
        this.medAvailability = medAvailability;
        this.medPrice = medPrice;
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

    public String getMedDescription() {
        return medDescription;
    }

    public void setMedDescription(String medDescription) {
        this.medDescription = medDescription;
    }

    public String getMedQuantity() {
        return medQuantity;
    }

    public void setMedQuantity(String medQuantity) {
        this.medQuantity = medQuantity;
    }
}
