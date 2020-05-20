package com.collection.sanjivani;

import java.io.Serializable;

public class CartInfo implements Serializable {
    public String medName, medPrice, medQuantity, medItemCount;

    public CartInfo() {

    }

    public CartInfo(String medName, String medQuantity, String medPrice) {
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

    public String getMedPrice() {
        return medPrice;
    }

    public void setMedPrice(String medPrice) {
        this.medPrice = medPrice;
    }

    public String getMedQuantity() {
        return medQuantity;
    }

    public void setMedQuantity(String medQuantity) {
        this.medQuantity = medQuantity;
    }

    public String getMedItemCount() {
        return medItemCount;
    }

    public void setMedItemCount(String medItemCount) {
        this.medItemCount = medItemCount;
    }
}
