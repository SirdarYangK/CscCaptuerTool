package com.csc.capturetool.myapplication.modules.action.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SirdarYangK on 2018/11/5
 * des:
 */
public class DeviceInfoBean implements Parcelable{
    public DeviceInfoBean() {
    }
    private String id;
    private String device_id;//设备id
    private String site_id;//投站ID
    private String status;//椅子状态
    private String update_time;//
    private String position;//位置id
    private String mac_id;//蓝牙地址
    private String site_name;//具体位置

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMac_id() {
        return mac_id;
    }

    public void setMac_id(String mac_id) {
        this.mac_id = mac_id;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    protected DeviceInfoBean(Parcel in) {
        id = in.readString();
        device_id = in.readString();
        site_id = in.readString();
        status = in.readString();
        update_time = in.readString();
        position = in.readString();
        mac_id = in.readString();
        site_name = in.readString();
    }

    public static final Creator<DeviceInfoBean> CREATOR = new Creator<DeviceInfoBean>() {
        @Override
        public DeviceInfoBean createFromParcel(Parcel in) {
            return new DeviceInfoBean(in);
        }

        @Override
        public DeviceInfoBean[] newArray(int size) {
            return new DeviceInfoBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(device_id);
        dest.writeString(site_id);
        dest.writeString(status);
        dest.writeString(update_time);
        dest.writeString(position);
        dest.writeString(mac_id);
        dest.writeString(site_name);
    }

    @Override
    public String toString() {
        return "DeviceInfoBean{" +
                "id='" + id + '\'' +
                ", device_id='" + device_id + '\'' +
                ", site_id='" + site_id + '\'' +
                ", status='" + status + '\'' +
                ", update_time='" + update_time + '\'' +
                ", position='" + position + '\'' +
                ", mac_id='" + mac_id + '\'' +
                ", site_name='" + site_name + '\'' +
                '}';
    }
}
