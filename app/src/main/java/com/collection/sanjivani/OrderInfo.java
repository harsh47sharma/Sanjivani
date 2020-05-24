package com.collection.sanjivani;

import java.io.Serializable;

public class OrderInfo implements Serializable {

    public String orderDate, orderId, orderStatus, orderTime, orderTotal, medName, medItemCount, medPrice;

    public OrderInfo(String orderDate, String orderId, String orderStatus, String orderTime, String orderTotal) {
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
        this.orderTotal = orderTotal;
    }

    public OrderInfo() {
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getMedItemCount() {
        return medItemCount;
    }

    public void setMedItemCount(String medItemCount) {
        this.medItemCount = medItemCount;
    }

    public String getMedPrice() {
        return medPrice;
    }

    public void setMedPrice(String medPrice) {
        this.medPrice = medPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }
}
