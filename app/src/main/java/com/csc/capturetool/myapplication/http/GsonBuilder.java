package com.csc.capturetool.myapplication.http;

import com.google.gson.Gson;

/**
 * @author yangkun
 * @time 2018/10/23
 */

public class GsonBuilder {
    public static Gson buildGson() {
        return new com.google.gson.GsonBuilder()
                .registerTypeHierarchyAdapter(HttpResult.class, new ResultJsonDeser())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
    }
}
