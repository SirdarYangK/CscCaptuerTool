package com.csc.capturetool.myapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by SirdarYangK on 2018/11/6
 * des: 时间工具类
 */
public class DateUtils {
    private static DateUtils instance;
    public static synchronized DateUtils getInstance() {
        if (instance == null) {
            instance = new DateUtils();
        }
        return instance;
    }

    /**
     * 获取本地当前时间戳
     *
     * @return
     */
    public String getDateParam() {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            String localeTime = sdr.format(new Date());
            date = sdr.parse(localeTime);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }
}
