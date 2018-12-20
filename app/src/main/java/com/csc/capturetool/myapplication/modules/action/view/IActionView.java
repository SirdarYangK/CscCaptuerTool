package com.csc.capturetool.myapplication.modules.action.view;

import com.clj.fastble.data.BleDevice;
import com.csc.capturetool.myapplication.base.IBaseView;
import com.csc.capturetool.myapplication.modules.action.model.DeviceInfoBean;

import java.util.ArrayList;

/**
 * Created by SirdarYangK on 2018/11/5
 * des:
 */
public interface IActionView extends IBaseView {

    void onlineState(String isOnline);

    int getWriteTime();

    void initTextView(DeviceInfoBean deviceInfoBean);

    void bleList(ArrayList<BleDevice> list);
}
