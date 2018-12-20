package com.csc.capturetool.myapplication.widget.autolayout.utils;

import android.util.Log;

/**
 * Created by KunY on 2018-5-18.
 *
 */
public class L
{
    public static boolean debug = false;
    private static final String TAG = "AUTO_LAYOUT";

    public static void e(String msg)
    {
        if (debug)
        {
            Log.e(TAG, msg);
        }
    }


}
