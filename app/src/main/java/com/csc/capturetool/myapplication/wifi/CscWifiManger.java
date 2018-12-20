package com.csc.capturetool.myapplication.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.csc.capturetool.myapplication.utils.DataFormat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by linjie on 2018/4/18.
 * wifi管理组件
 */
public class CscWifiManger {

    private final static String TAG = "CSCLAUNCHER";

    // wifi管理器
    private WifiManager mWifiManager = null;

    private AtomicBoolean mConnected = new AtomicBoolean(false);

    // 上下文
    private Context mContext = null;

    // 消息处理
    private Handler mHandler = null;

    // 当前wifi连接的状态 1 - onSuccess  2 - onFailed
    private int status = 0;

    // 没有加密
    private static final int WIFICIPHER_NOPASS = 0;

    // wep加密
    private static final int WIFICIPHER_WEP = 1;

    // wpa加密
    private static final int WIFICIPHER_WPA = 2;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dealWifiBdReceiver(context, intent);
        }
    };

    //    private WifiManager.ActionListener mConnectListener = new WifiManager.ActionListener() {
    //        @Override
    //        public void onSuccess() {
    //            Log.i(TAG, "onSuccess");
    //            status = 1;
    //        }
    //
    //        @Override
    //        public void onFailure(int reason) {
    //            Log.i(TAG, "onFailure reason:  " + reason);
    //            status = 2;
    //            ;
    //        }
    //    };
    //    private WifiManager.ActionListener mForgetListener = new WifiManager.ActionListener() {
    //        @Override
    //        public void onSuccess() {
    //
    //        }
    //
    //        @Override
    //        public void onFailure(int reason) {
    //
    //        }
    //    };

    public CscWifiManger(Context context, Handler handler) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mHandler = handler;
        mContext = context;
        registerReceiver();
    }

    /**
     * 设置wifi是否可用
     *
     * @param enable 设置wifi是否可用
     * @return 是否开关成功
     */
    public boolean setWifiEnabled(boolean enable) {
        return mWifiManager.setWifiEnabled(enable);
    }

    /**
     * 获取wifi是否可用
     *
     * @return wifi状态
     */
    public boolean getWifiEnabled() {
        boolean result = false;
        int wifiState = mWifiManager.getWifiState();
        if (wifiState == WifiManager.WIFI_STATE_ENABLED || wifiState == WifiManager.WIFI_STATE_ENABLING) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * 获取预置的wifi
     *
     * @return 预置的wifi
     */
    public PadWiFiInfo getCurrentWifi() {
        PadWiFiInfo info = null;
        List<WifiConfiguration> wifiConfigs = mWifiManager.getConfiguredNetworks();
        if (wifiConfigs != null) {
            for (int i = wifiConfigs.size() - 1; i >= 0; i--) {
                WifiConfiguration wifiConfiguration = wifiConfigs.get(i);
                if (wifiConfiguration.status == WifiConfiguration.Status.CURRENT) {
                    info = new PadWiFiInfo(wifiConfiguration.SSID, null, 1);
                    break;
                }
            }
        }
        return info;
    }


    /**
     * 根据密码连接指定SSID的wifi
     *
     * @param wifiInfo 要连接的ifi信息
     * @return 网络连接结果
     */
    public boolean connect(PadWiFiInfo wifiInfo) {
        boolean result = false;
        Log.i(TAG, "connect SSID:  " + wifiInfo.getSSID() + ";  password:  " + wifiInfo.getPassword());
        try {
            // 如果当前连接的wifi与要设置的wifi ssid一致，则将当前的wifi信息丢弃，重新连接
            WifiConfiguration wificonfiguration = null;
            if (DataFormat.isEmpty(wifiInfo.getPassword())) {
                wificonfiguration = findWifiConfigBySSID(wifiInfo.getSSID());
            } else {
                wificonfiguration = createWifiConfig(wifiInfo.getSSID(), wifiInfo.getPassword(), WIFICIPHER_WPA);
            }
            if (wificonfiguration != null) {
                status = 0;
                forget(wifiInfo.getSSID());
                //                mWifiManager.connect(wificonfiguration, mConnectListener);
                //yk
                Method connect = mWifiManager.getClass().getDeclaredMethod("connect",
                        WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
                if (connect != null) {
                    connect.setAccessible(true);
                    connect.invoke(mWifiManager, wificonfiguration, null);
                }
            }
            result = checkWifiIsConnected(wifiInfo.getSSID());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据密码连接指定SSID的wifi
     *
     * @param wifiInfo 要连接的ifi信息
     * @return 网络连接结果
     */
    public int setWifiAuto(PadWiFiInfo wifiInfo) {
        int result = -1;
        Log.i(TAG, "connect SSID:  " + wifiInfo.getSSID() + ";  password:  " + wifiInfo.getPassword());
        try {
            // 如果当前连接的wifi与要设置的wifi ssid一致，则将当前的wifi信息丢弃，重新连接
            WifiConfiguration wificonfiguration = null;
            if (DataFormat.isEmpty(wifiInfo.getPassword())) {
                wificonfiguration = findWifiConfigBySSID(wifiInfo.getSSID());
            } else {
                wificonfiguration = createWifiConfig(wifiInfo.getSSID(), wifiInfo.getPassword(), WIFICIPHER_WPA);
            }
            forget(wifiInfo.getSSID());
            return mWifiManager.addNetwork(wificonfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 连接wifi
     *
     * @param id wifi id
     */
    public boolean connectWifiAuto(int id) {
        return mWifiManager.enableNetwork(id, true);
    }

    /**
     * 注销广播监听
     */
    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    /**
     * 注册广播监听
     */
    private void registerReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        //        mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        //        mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, mFilter);
    }

    /**
     * 丢弃指定ssid的wifi保存信息
     *
     * @param SSID wifi 的ssid
     */
    private void forget(String SSID) {
        //        WifiConfiguration config = isExist(SSID);
        //        if (config != null) {
        //            mWifiManager.forget(config.networkId, mForgetListener);
        //        }

        //yk
        WifiConfiguration config = isExist(SSID);
        if (config != null) {
            try {
                Method forget = mWifiManager.getClass().getDeclaredMethod("forget", int.class,
                        Class.forName("android.net.wifi.WifiManager$ActionListener"));
                if (forget != null) {
                    forget.setAccessible(true);
                    forget.invoke(mWifiManager, config.networkId, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检测ssid wifi是否连接成功,如果超过15s扔没有检测到成功，则为连接wifi失败
     *
     * @param ssid 要连接的wifi ssid
     * @return 是否连接成功
     */
    private boolean checkWifiIsConnected(String ssid) {
        boolean result = false;
        long startTime = System.currentTimeMillis();
        while (true && status != 2) {
            if ((System.currentTimeMillis() - startTime < 10 * 1000)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
            List<WifiConfiguration> wifiConfigs = mWifiManager.getConfiguredNetworks();
            for (int i = wifiConfigs.size() - 1; i >= 0; i--) {
                WifiConfiguration wifiConfiguration = wifiConfigs.get(i);
                if (wifiConfiguration.SSID.equals(convertToQuotedString(ssid))
                        && wifiConfiguration.status == WifiConfiguration.Status.CURRENT) {
                    result = true;
                    break;
                }
            }
        }
        Log.i(TAG, "checkWifiIsConnected result:  " + result);
        return result;
    }

    /**
     * 根据ssid找到wifi
     *
     * @param ssid wifi ssid
     * @return wifi config
     */
    private WifiConfiguration findWifiConfigBySSID(String ssid) {
        WifiConfiguration config = null;
        List<WifiConfiguration> wifiConfigs = mWifiManager.getConfiguredNetworks();
        for (int i = wifiConfigs.size() - 1; i >= 0; i--) {
            WifiConfiguration wifiConfiguration = wifiConfigs.get(i);
            Log.i(TAG, "findWifiConfigBySSID SSID:  " + wifiConfiguration.SSID + ";  ssid:  " + ssid);
            if (wifiConfiguration.SSID.equals(convertToQuotedString(ssid))) {
                config = wifiConfiguration;
                break;
            }
        }
        return config;
    }

    /**
     * 发送消息到主线程
     *
     * @param obj  消息的obj
     * @param what 消息类型
     */
    private void sendMessage(Object obj, int what) {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            message.obj = obj;
            message.what = what;
            message.sendToTarget();
        }
    }

    /**
     * 处理无线网络改变的广播
     *
     * @param context
     * @param intent
     */
    protected void dealWifiBdReceiver(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int messageWhat = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            sendMessage(null, messageWhat);
        }
        //        else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
        //                || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
        //                || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
        //            updateAccessPoints();
        //        }
        //yk
        else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            updateAccessPoints();
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            SupplicantState state = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            //            if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
            //            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
            updateAccessPoints();
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
        }
    }

    /**
     * 修改wifi状态
     */
    private void updateAccessPoints() {
        int wifiState = mWifiManager.getWifiState();
        // 有如下几种状态
        // WifiManager.WIFI_STATE_ENABLED;
        // WifiManager.WIFI_STATE_ENABLING;
        // WifiManager.WIFI_STATE_DISABLING;
        // WifiManager.WIFI_STATE_DISABLED；
        sendMessage(null, wifiState);
    }

    /**
     * 根据ssid 和密码以及加密类型创建WifiConfiguration
     *
     * @param ssid     wifi 的 ssid
     * @param password wifi 的密码
     * @param type     wifi 的加密类型
     * @return 指定wifi的配置参数对象
     */
    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        // 初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        // 指定对应的SSID
        config.SSID = "\"" + ssid + "\"";
        // 如果之前有类似的配置
        WifiConfiguration tempConfig = isExist(ssid);
        if (tempConfig != null) {
            //则清除旧有配置
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        // 需要密码
        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == WIFICIPHER_WEP) {
            // WEP加密
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            // WPA加密（包括WPA2)
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 判断指定ssid是否在已经保存过的wifi列表中
     *
     * @param ssid wifi的ssid
     * @return 查询结果
     */
    private WifiConfiguration isExist(String ssid) {
        WifiConfiguration result = null;
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (!DataFormat.isEmpty(configs)) {
            for (WifiConfiguration config : configs) {
                if (config.SSID.equals("\"" + ssid + "\"")) {
                    result = config;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 将ssid添加“”
     *
     * @param content ssid
     * @return 格式化之后的ssid
     */
    private String convertToQuotedString(String content) {
        if (!content.contains("\"")) {
            content = "\"" + content + "\"";
        }
        return content;
    }

}