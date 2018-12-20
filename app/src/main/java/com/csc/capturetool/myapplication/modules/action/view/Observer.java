package com.csc.capturetool.myapplication.modules.action.view;


import com.clj.fastble.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
