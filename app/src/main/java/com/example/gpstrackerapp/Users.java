package com.example.gpstrackerapp;

public class Users {

    private String username, origin, destination,phone,car;

    public Users(){

    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

//    public String getDestination() {
//        return destination;
//    }
//
//    public void setDestination(String destination) {
//        this.destination = destination;
//    }
//
//    public String getOrigin() {
//        return origin;
//    }
//
//    public void setOrigin(String origin) {
//        this.origin = origin;
//    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public Users(String username, String phone) {
        this.username = username;
//        this.origin = origin;
//        this.destination = destination;
        this.phone=phone;
        this.car=car;
    }
}
