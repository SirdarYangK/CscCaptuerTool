package com.csc.capturetool.myapplication.utils;

import android.util.Base64;

import com.csc.capturetool.myapplication.constansts.CaptureToolConstanst;
import com.socks.library.KLog;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Created by SirdarYangK on 2018/11/6
 * des: 字符串工具类
 */
public class StringUtils {
    public static String number;
    public static String time;
    private static String substring;

    public static Map<String, String> paramsMapBasic(Map<String, String> paramsMapGet) {
        paramsMapGet.put("k", CaptureToolConstanst.HWMVLPVC);
        paramsMapGet.put("n", StringUtils.generateRandomNumber());
        paramsMapGet.put("t", DateUtils.getInstance().getDateParam());
        paramsMapGet.put("v", CaptureToolConstanst.INTERFACE_VERSION);
        return paramsMapGet;
    }

    /**
     * 获取sign 参数
     *
     * @param paramMap
     * @return
     */
    public static String getSignParam(Map<String, String> paramMap) {
        StringBuilder stringBuffer = new StringBuilder();
        Map<String, String> map = compare(paramMap);
        map.put("key", CaptureToolConstanst.HWMVLPVC_PRIVATE_KEY);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            stringBuffer.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        String string = stringBuffer.toString();
        if (string.endsWith("&")) {
            substring = string.substring(0, string.length() - 1);
        }
        KLog.w("basic_param：    " + substring);
        //清除stringBuffer缓存
        stringBuffer.delete(0, stringBuffer.length());
        String sign = Objects.requireNonNull(MD5.MD5_32(substring)).toUpperCase();

        //        StringBuffer paramSB = new StringBuffer();
        //        for (Map.Entry<String, String> entry : map.entrySet()) {
        //            paramSB.append(entry.getKey() + ": " + entry.getValue() + "; ");
        //        }
        //        KLog.w("sign：   " + sign + " ==== " + paramSB.toString());
        KLog.w("sign：   " + sign);
        return sign;
    }

    /**
     * 产生 N 位随机数
     *
     * @return
     */
    public static String generateRandomNumber() {
        //生成8至32位随机数
        Random rand = new Random();
        int randNum = rand.nextInt(25) + 8;

        String strRand = "";
        for (int i = 0; i < randNum; i++) {
            strRand += String.valueOf((int) (Math.random() * 10));
        }
        return strRand;
    }

    /**
     * map集合自然排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> compare(Map map) {
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        //升序排序
        Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> mapping : list) {
            newMap.put(mapping.getKey(), mapping.getValue());
        }
        return newMap;
    }

    /**
     * base64加密
     *
     * @param bytes
     * @return
     */
    public static String base64Encode(byte[] bytes) {
        byte[] encodedBytes = Base64.encode(bytes, Base64.DEFAULT);
        return new String(encodedBytes, Charset.forName("UTF-8"));
    }

    /**
     * base64解密
     *
     * @param str
     * @return
     */
    public static String base64Decode(String str) {
        byte[] decode = Base64.decode(str, Base64.DEFAULT);
        return new String(decode, Charset.forName("UTF-8"));
    }

    /**
     * 放连击
     */
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;

    }
}
