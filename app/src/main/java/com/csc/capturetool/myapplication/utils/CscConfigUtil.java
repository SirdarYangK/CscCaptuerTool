package com.csc.capturetool.myapplication.utils;

import android.content.Context;

import com.csc.capturetool.myapplication.constansts.PadParamsConstanst;
import com.csc.capturetool.myapplication.utils.xml.XmlEditor;
/**
 * Created by SirdarYangK on 2018/11/2
 * des: 配置参数操作类
 */
public class CscConfigUtil {

    // xml编辑器
    private static XmlEditor xmlEditor = null;

    // 偏好设置
    private static SharedPreferenceUtil mUtil = null;

    static {
        xmlEditor = XmlEditor.getXmlEditor(CscFileUtil.getConfilePath());
    }

    public static void init(Context mContext){
        mUtil = new SharedPreferenceUtil(mContext);
    }

    /**
     * 设置String 字符串
     *
     * @param key   保存的key
     * @param value 保存的value
     */
    public static void configString(String key, String value) {
        mUtil.saveString(key,value);
    }

    /**
     * 获取key对应的值
     */
    public static String getString(String key) {
        return mUtil.getString(key);
    }

    /**
     * 清除配对的相关信息
     */
    public static void clearMatchInfo(){
        CscConfigUtil.configString(PadParamsConstanst.KEY_CHAIR_ID , "");
        CscConfigUtil.configString(PadParamsConstanst.KEY_PAD_ADDRESS_CONTENT , "");
        CscConfigUtil.configString(PadParamsConstanst.KEY_PAD_AREA_ID , "");
        CscConfigUtil.configString(PadParamsConstanst.KEY_PAD_STATUS , "");
        CscConfigUtil.configString(PadParamsConstanst.KEY_PAD_ID , "");
    }

}
