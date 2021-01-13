package com.pjt.rebis.ui.history;

    /* 'RentalItem' object constructor class. */
public class RentalItem {
    private String Renter, Customer;
    private String Addressrenter, Addresscustomer, Date, State, Bike;
    private int ID;
    private int Days;
    private double Fee, Deposit;

    public RentalItem() { }

    public RentalItem(String _Renter, String _Customer, String _Addressrenter, String _Addresscustomer,
                        int _ID, String _Date, int _Days, double _Fee, double _Deposit, String _State, String _Bike) {
        this.Renter = _Renter;
        this.Customer = _Customer;
        this.Addressrenter = _Addressrenter;
        this.Addresscustomer = _Addresscustomer;
        this.ID = _ID;
        this.Date = _Date;
        this.Days = _Days;
        this.Fee = _Fee;
        this.Deposit = _Deposit;
        this.State = _State;
        this.Bike = _Bike;
    }

    public String getRenter() {
        return this.Renter;
    }

    public String getCustomer() {
        return this.Customer;
    }

    public String getAddressRenter() {
        return this.Addressrenter;
    }

    public String getAddressCustomer() {
        return this.Addresscustomer;
    }

    public int getID() {
        return this.ID;
    }

    public String getDate() {
        return this.Date;
    }

    public int getDays() {
        return this.Days;
    }

    public double getFee() {
        return this.Fee;
    }

    public double getDeposit() {
        return this.Deposit;
    }

    public String getState() {
        return this.State;
    }

    public String getBike() {
            return this.Bike;
        }

}
