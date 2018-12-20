package com.csc.capturetool.myapplication.wifi;

/**
 * Created by linjie on 2018/4/21.
 * wifi数据
 */

public class PadWiFiInfo {

    // ssid
    private String SSID = null;

    // 密码
    private String password = null;

    // 优先级
    private int level = 1;

    public PadWiFiInfo(String SSID, String password, int level) {
        this.SSID = SSID;
        this.password = password;
        this.level = level;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "PadWiFiInfo{" +
                "SSID='" + SSID + '\'' +
                ", password='" + password + '\'' +
                ", level=" + level +
                '}';
    }
}
