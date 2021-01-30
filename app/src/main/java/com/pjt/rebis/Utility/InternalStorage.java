package com.pjt.rebis.Utility;

import android.content.Context;

import com.pjt.rebis.ui.profile.WalletItem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

public class InternalStorage {
    //final static String FILEUSER_ADDRESSESLIST = null;
    public static final String layendarmal = "MEfXKy!(&c7}7FH]G~k}";
    static Hashtable<String, String> walletList = null;

    public static Hashtable<String, String> getWalletList(Context mContext, String FILEUSER_ADDRESSESLIST) {
        FileInputStream stream = null;

        try {
            stream = mContext.openFileInput(FILEUSER_ADDRESSESLIST);
            ObjectInputStream din = new ObjectInputStream(stream);
            walletList = (Hashtable<String, String>) din.readObject();
            stream.getFD().sync();
            stream.close();
            din.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        if (walletList == null) {
            walletList = new Hashtable<>();
        }

        return walletList;
    }

    private static void updateWalletList(Context mContext, String FILEUSER_ADDRESSESLIST) {
        FileOutputStream stream = null;

        try {
            stream = mContext.openFileOutput(FILEUSER_ADDRESSESLIST, Context.MODE_PRIVATE);
            ObjectOutputStream dout = new ObjectOutputStream(stream);
            dout.writeObject(walletList);
            stream.getFD().sync();
            stream.close();
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void clearWalletList(Context mContext, String FILEUSER_ADDRESSESLIST) {
        getWalletList(mContext, FILEUSER_ADDRESSESLIST).clear();
        updateWalletList(mContext, FILEUSER_ADDRESSESLIST);
    }

    public static void addToWalletList(Context mContext, String FILEUSER_ADDRESSESLIST, WalletItem obj) {
        getWalletList(mContext, FILEUSER_ADDRESSESLIST).put(obj.getAddress(), obj.getMnemonic());
        updateWalletList(mContext, FILEUSER_ADDRESSESLIST);
    }

    public static String getWalletKey(Context mContext, String FILEUSER_ADDRESSESLIST) {
        String walletKey = getWalletList(mContext, FILEUSER_ADDRESSESLIST).get("walletKey");
        return walletKey;
    }

    public static void setWalletKey(Context mContext, String FILEUSER_ADDRESSESLIST, String walletKey) {
        getWalletList(mContext, FILEUSER_ADDRESSESLIST).put("walletKey", walletKey);
        updateWalletList(mContext, FILEUSER_ADDRESSESLIST);
    }

    public static boolean doesWalletKeyExist(Context mContext, String FILEUSER_ADDRESSESLIST) {
        try {
            String walletKey = getWalletList(mContext, FILEUSER_ADDRESSESLIST).get("walletKey");
            return (walletKey.length() > 10);
        } catch (Exception e) {
            return false;
        }
    }

}
