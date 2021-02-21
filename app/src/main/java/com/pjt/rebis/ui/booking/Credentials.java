package com.pjt.rebis.ui.booking;

public class Credentials {
    private String Username, Usertype;

    public Credentials() {
    }

    public Credentials(String Username, String Usertype) {
        this.Username = Username;
        this.Usertype = Usertype;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUsertype() {
        return Usertype;
    }

    public void setUsertype(String usertype) {
        Usertype = usertype;
    }
}
