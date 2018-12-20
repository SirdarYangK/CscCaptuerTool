package com.csc.capturetool.myapplication.utils;

import android.content.Context;
import android.os.Environment;

/**
 * Created by SirdarYangK on 2018/11/2
 * des: 文件操作
 */

public class CscFileUtil {

    /**
     * 获取config配置文件路径
     * @return 配置文件的路径
     */
    public static String getConfilePath(){
        return Environment.getExternalStorageDirectory() + "/salim/config/csc_config.xml";
    }

    /**
     * 获取应用缓存文件的路径
     * @param mContext 上下文
     * @return 缓存文件的路径
     */
    public static String getFileCachePath(Context mContext){
        return Environment.getExternalStorageDirectory() + "/salim" + mContext.getPackageName();
    }

}
