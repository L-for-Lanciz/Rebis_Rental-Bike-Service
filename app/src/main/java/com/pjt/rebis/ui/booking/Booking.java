package com.pjt.rebis.ui.booking;

import java.util.Calendar;

public class Booking {
    private String customer, renter, renterUID, bike, time;
    private int key;

    public Booking() {
    }

    public Booking(String customer, String renter, String renterUID, String bike, String time, int key) {
        this.customer = customer;
        this.renter = renter;
        this.renterUID = renterUID;
        this.bike = bike;
        this.time = time;
        this.key = key;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getRenter() {
        return renter;
    }

    public void setRenter(String renter) {
        this.renter = renter;
    }

    public String getRenterUID() {
        return renterUID;
    }

    public void setRenterUID(String renterUID) {
        this.renterUID = renterUID;
    }

    public String getBike() {
        return bike;
    }

    public void setBike(String bike) {
        this.bike = bike;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
