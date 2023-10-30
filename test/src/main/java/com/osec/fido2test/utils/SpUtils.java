package com.osec.fido2test.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.osec.fido2test.MyApplication;

public class SpUtils {
    private final static String SP_NAME = "cache";

    public static synchronized void putString(String key, String value) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public static synchronized String getString(String key) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }
}
