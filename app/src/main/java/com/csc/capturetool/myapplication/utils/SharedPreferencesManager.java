package com.csc.capturetool.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.csc.capturetool.myapplication.CscApplication;

/**
 * Created by SirdarYangK on 2018/11/2
 * des: sp
 */

public class SharedPreferencesManager {

    private static SharedPreferences sharedPreferences = CscApplication.getPreferences();

    public static boolean putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static boolean putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(String key) {
        return getInt(key, -1);
    }

    public static int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static boolean putLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static long getLong(String key) {
        return getLong(key, -1L);
    }

    public static long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static boolean putFloat(Context context, String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public static float getFloat(String key) {
        return getFloat(key, -1.0F);
    }

    public static float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public static boolean putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
