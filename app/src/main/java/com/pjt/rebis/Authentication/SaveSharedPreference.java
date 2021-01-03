package com.pjt.rebis.Authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

    /* Allows to store some useful variable as 'data cache' on the device. That allows to speed up
    *  some operations, avoiding redundant queries to the database. */
public class SaveSharedPreference {
    static final String PREF_USER_NAME= "username";
    static final String PREF_USER_TYPE = "usertype";
    static final String PREF_PROPIC = "propic";
    static final String PREF_EMAIL = "email";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }


    public static void clearPreferences(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        // editor.putString(PREF_EMAIL, "");
        editor.commit();
    }

    public static void setUserType(Context ctx, String userType) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_TYPE, userType);
        editor.commit();
    }

    public static String getUserType(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_USER_TYPE, "");
    }

    public static void setUserPropic(Context ctx, String propic) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PROPIC, propic);
        editor.commit();
    }

    public static String getUserPropic(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_PROPIC, "");
    }

    public static void setEmail(Context ctx, String _email) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL, _email);
        editor.commit();
    }

    public static String getEmail(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }

}