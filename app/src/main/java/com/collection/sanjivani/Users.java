package com.collection.sanjivani;

public class Users {
    private String userName, userEmail, userAddress, userPhoneNumber;

    public Users(){

    }

    public Users(String userName, String userEmail, String userAddress, String userPhoneNumber) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }
}
