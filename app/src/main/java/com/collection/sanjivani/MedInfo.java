package com.collection.sanjivani;

import java.io.Serializable;

public class MedInfo implements Serializable {
    public String medName, medAvailability, medPrice;
    public String medDescription, medQuantity, navList;

    public MedInfo(){

    }

    public MedInfo(String medName, String medQuantity, String medPrice) {
        this.medName = medName;
        this.medQuantity = medQuantity;
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

    public String getNavList() {
        return navList;
    }

    public void setNavList(String navList) {
        this.navList = navList;
    }
}
