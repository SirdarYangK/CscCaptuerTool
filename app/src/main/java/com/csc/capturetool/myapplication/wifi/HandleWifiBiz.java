package com.csc.capturetool.myapplication.wifi;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.csc.capturetool.myapplication.constansts.WifiStatusConstants;
import com.csc.capturetool.myapplication.utils.DataFormat;
import com.csc.capturetool.myapplication.utils.ExecutorInstance;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by SirdarYangK on 2018/11/2
 * des:处理wifi事件
 */

public class HandleWifiBiz {

    private final static String TAG = "CSCLAUNCHER";

    // 上下文
    private Context mContext = null;

    // wifi管理器
    private CscWifiManger mWifiManager = null;

    // 消息处理
    private Handler mHandler = null;

    public HandleWifiBiz(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mWifiManager = new CscWifiManger(mContext, mHandler);
    }

    /**
     * 获取当前网络的ssid
     */
    public PadWiFiInfo getCurrentWifi() {
        return mWifiManager.getCurrentWifi();
    }

    /**
     * 预置初始wifi给平板
     */
    public void setOriginalWifi() {
        PadWiFiInfo bakInfo = new PadWiFiInfo("iChair01-5G", "iChair01-5G", 1);
        List<PadWiFiInfo> wifiList = new ArrayList<PadWiFiInfo>();
        wifiList.add(bakInfo);
        connectWifi(wifiList);
    }

    /**
     * 连接扫码wifi
     *
     * @param stringExtra 加密过的wifi和密码
     */
    public void setScanWifi(String stringExtra) {
        try {
            String decode = DataFormat.base64Decode(stringExtra);
            JSONObject object = new JSONObject(decode);
            String ssid = object.getString("ssid");
            String pwd = object.getString("pwd");
            PadWiFiInfo bakInfo = new PadWiFiInfo(ssid, pwd, 1);
            List<PadWiFiInfo> wifiList = new ArrayList<>();
            wifiList.add(bakInfo);
            connectWifi(wifiList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据wifi名称以及密码连接指定wifi
     *
     * @param wifiList wifi列表
     */
    public void connectWifi(final List<PadWiFiInfo> wifiList) {
        ExecutorInstance.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                boolean isWifiOpen = checkWifiIsOpened();
                Log.i(TAG, "isWifiOpen:  " + isWifiOpen);
                if (isWifiOpen) {
                    Log.i(TAG, "wifiList:  " + wifiList);
                    if (!DataFormat.isEmpty(wifiList)) {
                        sortedWifiListByLevel(wifiList);
                        // 保存当前连接的wifi,如果要连接的这一组wifi都没有成功，则还原到之前好的wifi
                        PadWiFiInfo currentWifi = mWifiManager.getCurrentWifi();
                        Log.i(TAG, "currentWifi:  " + currentWifi);
                        // 保存这一组中连接成功的wifi
                        PadWiFiInfo okWifi = null;
                        for (int i = 0; i < wifiList.size(); i++) {
                            PadWiFiInfo info = wifiList.get(i);
                            Log.i(TAG, "current connect wifi:  " + info);
                            boolean result = mWifiManager.connect(info);
                            Log.i(TAG, "wifi:  " + info.getSSID() + ";  result:  " + result);
                            if (result) {
                                okWifi = info;
                            } else if (i == wifiList.size() - 1 && okWifi != null) {
                                Log.i(TAG, "last wifi:  " + info.getSSID() + "  connect failed");
                                // 如果最后一个没有连接成功，则连接之前好的一个wifi
                                boolean temp = mWifiManager.connect(okWifi);
                                Log.i(TAG, "wifi:  " + okWifi.getSSID() + ";  result:  " + result);
                                if (!temp) {
                                    // 如果之前好的wifi仍然没有连接成功，认为本次连接失败
                                    okWifi = null;
                                }
                            }
                        }
                        // 如果这一组中的wifi都没有连接成功，则还原之前保存的连接成功的wifi
                        if (okWifi == null) {
                            if (currentWifi != null && mWifiManager.connect(currentWifi)) {
                                Log.i(TAG, "connect last wifi success:  " + currentWifi.getSSID());
                                sendMessage(WifiStatusConstants.MSG_CONNECT_WIFI_FAILED, "WiFi连接失败，重新连接到--" + currentWifi.getSSID());
                            } else {
                                sendMessage(WifiStatusConstants.MSG_CONNECT_WIFI_FAILED, "WiFi连接不成功，更换其他WiFi试试");
                            }
                        } else {
                            Log.i(TAG, "connect current wifi success:  " + okWifi.getSSID());
                            sendMessage(WifiStatusConstants.MSG_CONNECT_WIFI_SUCCESS, "WiFi连接成功，当前WiFi是--" + okWifi.getSSID());
                        }
                    }
                } else {
                    Log.i(TAG, "open wifi failed");
                    mHandler.sendEmptyMessage(WifiStatusConstants.MSG_OPEN_WIFI_FAILED);
                }
            }
        });
    }

    /**
     * 根据wifi名称以及密码连接指定wifi
     *
     * @param wifiList wifi列表
     */
    public void connectWifiAuto(final List<PadWiFiInfo> wifiList) {
        ExecutorInstance.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                boolean isWifiOpen = checkWifiIsOpened();
                sortedWifiListByLevel(wifiList);
                int id = -1;
                for (PadWiFiInfo info : wifiList) {
                    id = mWifiManager.setWifiAuto(info);
                }
                if (id != -1) {
                    mWifiManager.connectWifiAuto(id);
                }
            }
        });
    }

    /**
     * 发送消息
     *
     * @param what    消息类型
     * @param content 消息提示内容
     */
    private void sendMessage(int what, String content) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = content;
        mHandler.sendMessage(message);
    }

    /**
     * 根据wifi优先级排序，优先级低的先连接，优先级高的后连接，确保最后一个连接的是优先级最高的wifi
     */
    private void sortedWifiListByLevel(List<PadWiFiInfo> wifiList) {
        Collections.sort(wifiList, new Comparator<PadWiFiInfo>() {
            @Override
            public int compare(PadWiFiInfo o1, PadWiFiInfo o2) {
                return o1.getLevel() - o2.getLevel();
            }
        });
    }

    /**
     * 检测wifi是否已经打开,如果5s内仍没有打开，则失败
     *
     * @return wifi打开结果
     */
    private boolean checkWifiIsOpened() {
        boolean result = false;
        long startTime = System.currentTimeMillis();
        if (!mWifiManager.getWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            while (System.currentTimeMillis() - startTime < 5000) {
                if (mWifiManager.getWifiEnabled()) {
                    result = true;
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            result = true;
        }
        return result;
    }
}
