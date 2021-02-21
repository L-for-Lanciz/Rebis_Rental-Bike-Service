package com.pjt.rebis.ui.booking;

import com.pjt.rebis.ui.bikes.Bicicletta;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private Credentials Credentials;
    private PersonalData PersonalData;
    private HashMap<String, Bicicletta> Bikes = new HashMap<>();
    private Ratings Ratings;

    public User() {
    }

    public User(Credentials credentials, PersonalData personalData, Ratings ratings, HashMap<String, Bicicletta> bicicletta) {
        this.Credentials = credentials;
        this.PersonalData = personalData;
        this.Ratings = ratings;
        this.Bikes = bicicletta;
    }

    public Credentials getCredentials() {
        return Credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.Credentials = credentials;
    }

    public PersonalData getPersonalData() {
        return PersonalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.PersonalData = personalData;
    }

    public Ratings getRatings() {
        return Ratings;
    }

    public void setRatings(Ratings ratings) {
        this.Ratings = ratings;
    }

    public HashMap<String, Bicicletta> getBikes() {
        return Bikes;
    }

    public void setBikes(HashMap<String, Bicicletta> bikes) {
        Bikes = bikes;
    }
}
