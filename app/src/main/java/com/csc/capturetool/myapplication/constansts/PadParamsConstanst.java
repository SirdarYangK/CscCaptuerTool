package com.csc.capturetool.myapplication.constansts;

/**
 * Created by linjie on 2018/4/16.
 * 平板参数常量类
 */

public class PadParamsConstanst {

    /**
     * 存储椅子deviceid的key
     */
    public final static String KEY_CHAIR_ID = "CHAIR_ID";

    /**
     * 存储平板deviceid的key
     */
    public final static String KEY_PAD_ID = "PAD_ID";

    /**
     * 存储平板的位置
     */
    public final static String KEY_PAD_ADDRESS_CONTENT = "PAD_ADDRESS";

    /**
     * 存储平板的请求轮询
     */
    public final static String KEY_PAD_LOOP = "PAD_PAD_LOOP";

    /**
     * 存储平板的区域ID
     */
    public final static String KEY_PAD_AREA_ID = "PAD_AREA_ID";

    /**
     * 城市名称
     */
    public final static String KEY_PAD_CITY_NAME = "PAD_CITY_NAME";

    /**
     * 密码校验成功之后要进入的页面参数名称
     */
    public final static String KEY_PAGE_PARAM = "PAGE_PARAM";

    /**
     * 平板当前的状态
     */
    public final static String KEY_PAD_STATUS = "PAD_STATUS";

    /**
     * 进入安装工具之前的密码校验
     */
    public final static String ENTER_PASSWORD = "qwert" +
            "";

    /**
     * 点击wifi扫码要进入的页面参数名称
     */
    public final static String KEY_PAGE_PARAM_SCAN_WIFI = "PAGE_PARAM_SCAN_WIFI";

    /**
     * 点击wifi扫码获取到的wifi信息
     */
    public final static String KEY_WIFI_INFO = "WIFI_INFO";

    /**
     * 密码校验成功之后进入设备绑定列表
     */
    public final static int PARAM_TO_DEVICE_BIND_PAGE = 1;

    /**
     * 密码校验成功之后进入wifi列表
     */
    public final static int PARAM_TO_WIFI_PAGE = 2;

    /**
     * 密码校验成功之后进入暗门页面
     */
    public final static int PARAM_TO_HIDE_PAGE = 3;

    /**
     * 进入故障页面
     */
    public final static int PARAM_TO_TROUBLE_PAGE = 4;

    /**
     * 进入扫码连接页面
     */
    public final static int PARAM_TO_SCAN_PAGE = 5;

    /**
     * 扫码失败
     */
    public final static int PAD_TROUBLE_SCAN_FAILED = 31;

    /**
     * 绑定失败
     */
    public final static int PAD_TROUBLE_BIND_FAILED = 32;

    /**
     * 网络连接失败
     */
    public final static int PAD_TROUBLE_CONNECT_WIFI_FAILED = 33;

    /**
     * 打开app
     */
    public final static int PAD_TYPE_OPEN_APP = 1;

    /**
     * 打开天气
     */
    public final static int PAD_TYPE_OPEN_WEATHER = 0;

    /**
     * 打开网页
     */
    public final static int PAD_TYPE_OPEN_WEB = 2;

    /**
     * 打开更多应用
     */
    public final static int PAD_TYPE_OPEN_MORE = 3;

    /**
     * 打开图片
     */
    public final static int PAD_TYPE_OPEN_IMG = 4;

    /**
     * 打开可以缩放的图片
     */
    public final static int PAD_TYPE_OPEN_IMG_ZOOM = 5;
}
