package com.csc.capturetool.myapplication.http;

import com.csc.capturetool.myapplication.BuildConfig;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public class BaseUrls {
    public static final int RELEASE = 0;//正式环境
    private static final int DEV = 1;//测试环境

    public static final String CSC_BASE_URL = getCscBaseUrl();

    public static String getCscBaseUrl() {
        switch (BuildConfig.HTTP_TYPE) {
            case RELEASE:
            return  "https://m.csc33.cn";
            case DEV:
                return "https://mj.7i1.cn/";
            default:
                return "https://mj.7i1.cn/";
        }
    }
}
