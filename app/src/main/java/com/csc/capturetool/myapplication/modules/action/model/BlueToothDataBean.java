package com.csc.capturetool.myapplication.modules.action.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SirdarYangK on 2018/11/5
 * des:
 */
public class BlueToothDataBean implements Parcelable{
    /**
     * code : 0
     * devid : B643D2580200
     * udpsig : 154175457818926
     * data : Af+2Q9JYAgDwYDe8Fu848KOMs7dM/C2xGy8x6tsqNKoZkydidtHFbA==
     * data1 : 01ffb643d2580200f06037bc16ef38f0a38cb3b74cfc2db11b2f31eadb2a34aa1993276276d1c56c
     */

    private int code;
    private String devid;
    private String udpsig;
    private String data;
    private String data1;

    public BlueToothDataBean() {

    }

    protected BlueToothDataBean(Parcel in) {
        code = in.readInt();
        devid = in.readString();
        udpsig = in.readString();
        data = in.readString();
        data1 = in.readString();
    }

    public static final Creator<BlueToothDataBean> CREATOR = new Creator<BlueToothDataBean>() {
        @Override
        public BlueToothDataBean createFromParcel(Parcel in) {
            return new BlueToothDataBean(in);
        }

        @Override
        public BlueToothDataBean[] newArray(int size) {
            return new BlueToothDataBean[size];
        }
    };

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDevid() {
        return devid;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }

    public String getUdpsig() {
        return udpsig;
    }

    public void setUdpsig(String udpsig) {
        this.udpsig = udpsig;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(devid);
        dest.writeString(udpsig);
        dest.writeString(data);
        dest.writeString(data1);
    }

    @Override
    public String toString() {
        return "BlueToothDataBean{" +
                "code=" + code +
                ", devid='" + devid + '\'' +
                ", udpsig='" + udpsig + '\'' +
                ", data='" + data + '\'' +
                ", data1='" + data1 + '\'' +
                '}';
    }
}
