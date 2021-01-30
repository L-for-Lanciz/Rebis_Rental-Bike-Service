package com.pjt.rebis.ui.profile;

import java.io.Serializable;

/* This is a WalletItem constructor class. */
public class WalletItem implements Serializable {
    private String address;
    private String mnemonic;

    public WalletItem() { }

    public WalletItem(String address) {
        this.address = address;
    }

    public WalletItem(String address, String mnemonic) {
        this.address = address;
        this.mnemonic = mnemonic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }
}
