package com.csc.capturetool.myapplication.modules.action.model;

/**
 * Created by SirdarYangK on 2018/11/15
 * des:检查设备在线
 */
public class AliveBean {
    private String live;
    private String mac;

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "AliveBean{" +
                "live='" + live + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
