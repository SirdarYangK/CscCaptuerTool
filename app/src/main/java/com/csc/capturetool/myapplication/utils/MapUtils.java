package com.csc.capturetool.myapplication.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SirdarYangK on 2018/11/7
 * des:
 */
public class MapUtils {
    private Map<String, String> map = new HashMap<String, String>();
    private Object lock = new Object();
    private static MapUtils instance = new MapUtils();

    private MapUtils() {
    }

    public synchronized static MapUtils getInstance() {
        if (instance == null) {
            instance = new MapUtils();
        }
        return instance;
    }

    public void put(String taskId, String name) {
        synchronized (lock) {
            map.put(taskId, name);
        }
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void clearData() {
        map.clear();
    }


}
