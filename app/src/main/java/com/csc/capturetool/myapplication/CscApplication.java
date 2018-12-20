package com.csc.capturetool.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;


/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public class CscApplication extends Application {
    private static CscApplication mContext;
    private static String PREF_NAME = "csc_capture_pref";
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        ZXingLibrary.initDisplayOpinion(this);//二维码三方框架
        //自动适配设置
//        AutoLayoutConifg.getInstance().useDeviceSize();
    }

    public static CscApplication getApplication() {
        return mContext;
    }
    public static SharedPreferences getPreferences() {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return pref;
    }
}
