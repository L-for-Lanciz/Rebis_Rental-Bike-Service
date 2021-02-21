package com.pjt.rebis.ui.booking;

public class PersonalData {
    String address, city, country, fullname, phonenumber, postalcode;

    public PersonalData() {}

    public PersonalData(String _address, String _city, String _country, String _fullname, String _phonenumber, String _postalcode) {
        this.address = _address;
        this.city = _city;
        this.country = _country;
        this.fullname = _fullname;
        this.phonenumber = _phonenumber;
        this.postalcode = _postalcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }
}
