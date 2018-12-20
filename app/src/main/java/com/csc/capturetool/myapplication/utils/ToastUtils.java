package com.csc.capturetool.myapplication.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by SirdarYangK on 2018/11/2
 * des: Toast 单例工具类
 */
public class ToastUtils {

    private static CharSequence oldMsg;

    protected static Toast toast = null;

    private static long oneTime = 0;

    private static long twoTime = 0;

    //android系统设定的值
    private static final int LONG_DELAY = 3500; // 3.5 seconds

    private static final int SHORT_DELAY = 2000; // 2 seconds


    public static void showToast(final Context context, final CharSequence s, final int duration) {

        if ("main".equals(Thread.currentThread().getName())) {
            if (toast == null) {
                toast = Toast.makeText(context, null, duration);
                toast.setText(s);//避免小米手机toast带应用名称
                toast.show();
                oneTime = System.currentTimeMillis();
            } else {
                twoTime = System.currentTimeMillis();
                if (s.equals(oldMsg)) {
                    if (twoTime - oneTime > (duration == Toast.LENGTH_SHORT ? SHORT_DELAY : LONG_DELAY)) {
                        toast.show();
                    }
                } else {
                    oldMsg = s;
                    toast.setText(s);
                    toast.show();
                }
            }
            oneTime = twoTime;
        } else {
            Looper.prepare();
            if (toast == null) {
                toast = Toast.makeText(context, null, duration);
                toast.setText(s);
                toast.show();
                oneTime = System.currentTimeMillis();
            } else {
                twoTime = System.currentTimeMillis();
                if (s.equals(oldMsg)) {
                    if (twoTime - oneTime > (duration == Toast.LENGTH_SHORT ? SHORT_DELAY : LONG_DELAY)) {
                        toast.show();
                    }
                } else {
                    oldMsg = s;
                    toast.setText(s);
                    toast.show();
                }
            }
            oneTime = twoTime;
            Looper.loop();
        }
    }


    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getString(resId), duration);
    }

    public static void showToastShort(Context context, String resId) {
        showToast(context, resId, Toast.LENGTH_SHORT);
    }

    public static void showToastLong(Context context, String resId) {
        showToast(context, resId, Toast.LENGTH_LONG);
    }
}  