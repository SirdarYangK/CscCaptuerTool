package com.csc.capturetool.myapplication.constansts;

/**
 * Created by SirdarYangK on 2018/11/14
 * des: 关于蓝牙数据常量类
 */
public class BlueToothConstants {
    public final static String UUIDSERVICE = "0000fee7";//蓝牙server UUID
    public final static String UUIDWRITE = "0000fec6";//蓝牙写入数据 UUID
    public final static String UUIDNOTIFY = "0000fec5";//蓝牙开启通知读书数据 UUID
    public final static String TYPE_DATA = "0";//数据包类型
    public final static String TYPE_ANSWER = "1";//应答包类型
    public final static String LAUNCH_STATE = "1";//设备投放状态，只有1为正常
    public final static String ONLINE = "1";//在线状态，只有1为在线
    /**
     * 蓝牙控制设备类型及状态
     * 蓝牙->1  WiFi->2  启动->1  暂停->0  按摩椅->1  USB->2
     */
    public final static int BLE_START_CHAIR = 111;//BLE启动按摩椅
    public final static int BLE_END_CHAIR = 110;//BLE暂停按摩椅
    public final static int BLE_START_USB = 121;//BLE启动USB
    public final static int BLE_END_USB = 120;//BLE暂停USB
    public final static int BLE_RESTART = 0;//设备重启
    /**
     *发送蓝牙数据包后，后台返回设备状态码
     */
    public final static String DEVICE_CHAIR = "40";//按摩椅
    public final static String DEVICE_USB = "41";//USB
    public final static String DEVICE_ON = "01";//启动
    public final static String DEVICE_OFF = "03";//暂停

    /**
     * WiFi控制设备类型及状态
     */
    public final static String WIFI_START_CHAIR = "1";//WiFi启动按摩椅
    public final static String WIFI_END_CHAIR = "2";//WiFi暂停按摩椅
    public final static String WIFI_START_USB = "3";//WiFi启动USB
    public final static String WIFI_END_USB = "4";//WiFi暂停USB

}
