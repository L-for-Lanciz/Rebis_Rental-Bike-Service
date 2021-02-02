package com.pjt.rebis.webAPI;

import com.pjt.rebis.ui.history.RentalItem;

public class Payload {
    public RentalItem rentalItem;
    private String mnemonic;

    public Payload() {
    }

    public Payload(RentalItem robj, String mnemo) {
        this.rentalItem = robj;
        this.mnemonic = mnemo;
    }

    public RentalItem getRentalItem() {
        return rentalItem;
    }

    public String getMnemonic() {
        return mnemonic;
    }


}