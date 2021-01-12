package com.pjt.rebis.ui.bikes;

import java.util.Date;

public class Bicicletta {
    String name, brand, status, image;
    double value;
    int year, rentedCNT;

    public Bicicletta() {
    }

    public Bicicletta(String name, String brand, String status,
                      double value, int year, int rentedCNT, String image) {
        this.name = name;
        this.brand = brand;
        this.status = status;
        this.value = value;
        this.year = year;
        this.rentedCNT = rentedCNT;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRentedCNT() {
        return rentedCNT;
    }

    public void setRentedCNT(int rentedCNT) {
        this.rentedCNT = rentedCNT;
    }
}
