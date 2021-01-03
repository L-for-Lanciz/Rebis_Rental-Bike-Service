package com.pjt.rebis.ui.profile;

    /* This is a WalletItem constructor class. */
public class WalletItem {
    private String address;

    public WalletItem() { }

    public WalletItem(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
