package com.csc.capturetool.myapplication.utils;

import org.json.JSONObject;


/**
 * Created by SirdarYangK on 2018/11/6
 * des: Json工具类
 */
public class GetJsonUtils {

    /**
     * 获取公参
     *
     * @return
     */
    public static String getBasicParam(String function) {
        JSONObject jsonObject = new JSONObject();
        try {
            String md5Key = "HWMVLPVC";
            String md5Value = "1R35UYZCFPFCBN22KH4DWMBSA5NVQDCA";
            String number = StringUtils.generateRandomNumber();
            int time = Integer.parseInt(DateUtils.getInstance().getDateParam());

            String str = "devid=10010200003&k=HWMVLPVC&m=xcninfo&n=" + number + "&t="
                    + DateUtils.getInstance().getDateParam() + "&v=1&key=" + md5Key;
            String sign = MD5.MD5_32(str).toUpperCase();
            jsonObject.put("k", md5Key);
            jsonObject.put("m", function);
            jsonObject.put("n", number);
            jsonObject.put("t", time);
            jsonObject.put("v", "1");
            jsonObject.put("sign", sign);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
