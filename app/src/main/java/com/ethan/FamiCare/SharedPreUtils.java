package com.ethan.FamiCare;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreUtils {
    private static final String PREFERENCES = "live";
    private static SharedPreferences preferencesSharedPreferences;
    private static SharedPreferences getPreferences(Context context) {
        if (preferencesSharedPreferences == null) {
            preferencesSharedPreferences = context.getSharedPreferences(PREFERENCES, 0);
        }
        return preferencesSharedPreferences;
    }
    public static int getInt(Context context, String key, int defaultVal) {
        return getPreferences(context).getInt(key, defaultVal);
    }
    public static void setInt(Context context, String key, int value) {
        preferencesSharedPreferences.edit().putInt(key, value).commit();
    }

}

