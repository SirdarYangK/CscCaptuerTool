package com.csc.capturetool.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SharedPreferenceUtil {

    // 当前页面上下文
    private Context instance = null;

    public SharedPreferenceUtil(Context context) {
        instance = context;
    }

    public void saveString(String name, String value) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(
                "users", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putString(name, value);
        editor.commit();
    }

    public void saveBoolean(String name, boolean value) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(
                "users", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putBoolean(name, value);
        editor.commit();
    }

    public void saveLong(String name, long value) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(
                "users", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putLong(name, value);
        editor.commit();
    }

    public void saveInt(String name, int value) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(
                "users", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putInt(name, value);
        editor.commit();
    }

    public String getString(String name) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(
                "users", Context.MODE_PRIVATE);
        // getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
        String value = sharedPreferences.getString(name, "");
        return value;
    }

    public boolean getBoolean(String name) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(
                "users", Context.MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean(name, false);
        return value;
    }

    public long getLong(String name) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(
                "users", Context.MODE_PRIVATE);
        long value = sharedPreferences.getLong(name, 0l);
        return value;
    }

    public int getInt(String name) {
        SharedPreferences sharedPreferences = instance.getSharedPreferences(
                "users", Context.MODE_PRIVATE);
        int value = sharedPreferences.getInt(name, 0);
        return value;
    }

}
