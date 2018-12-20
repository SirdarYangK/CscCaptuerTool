package com.csc.capturetool.myapplication.service.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by SirdarYangK on 2018/11/5
 * des:蓝牙服务
 */
public class BluetoothService extends Service {

    // 蓝牙业务类
    private BluetoothBiz mBluetoothBiz = null;

    public class BluetoothBinder extends Binder {
        /**
         * 连接指定mac地址的蓝牙
         *
         * @param address mac地址
         */
        public void connect(String address) {
            mBluetoothBiz.connectByMacaddress(address);
        }

        public void disConnect() {
            mBluetoothBiz.disConnect();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new BluetoothBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    /**
     * 初始化蓝牙数据
     */
    private void initData() {
        mBluetoothBiz = new BluetoothBiz(this);
        mBluetoothBiz.init();
    }
}
