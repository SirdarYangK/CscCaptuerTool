package com.csc.capturetool.myapplication.modules.action.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.csc.capturetool.myapplication.R;
import com.csc.capturetool.myapplication.base.BaseActivity;
import com.csc.capturetool.myapplication.constansts.BlueToothConstants;
import com.csc.capturetool.myapplication.modules.action.presenter.ActionPresenter;
import com.csc.capturetool.myapplication.modules.action.view.IActionView;
import com.csc.capturetool.myapplication.modules.action.model.DeviceInfoBean;
import com.csc.capturetool.myapplication.modules.home.MainActivity;
import com.csc.capturetool.myapplication.utils.RxPermissionsUtils;
import com.csc.capturetool.myapplication.utils.StringUtils;
import com.csc.capturetool.myapplication.utils.ToastUtils;
import com.socks.library.KLog;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

public class ActionActivity extends BaseActivity<IActionView, ActionPresenter> implements
        IActionView, View.OnClickListener {
    private TextView tvTitle;
    private TextView tvDeviceId;
    private TextView tvAddress;
    private TextView tvOnline;
    private TextView tvState;
    private EditText etWriteTime;
    private ArrayList<BleDevice> mList = new ArrayList<>();
    private final static int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private final static int REQUEST_CODE_OPEN_GPS = 1;

    @Override
    protected int getContentView() {
        return R.layout.activity_action;
    }

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.app_name));
        tvDeviceId = findViewById(R.id.tv_device_id);
        tvDeviceId.setTextIsSelectable(true);
        tvAddress = findViewById(R.id.tv_address);
        tvState = findViewById(R.id.tv_state);
        tvOnline = findViewById(R.id.tv_online);
        etWriteTime = findViewById(R.id.et_write_time);
        findViewById(R.id.tv_scan).setOnClickListener(this);
        findViewById(R.id.bt_blue_start_chair).setOnClickListener(this);
        findViewById(R.id.bt_blue_start_usb).setOnClickListener(this);
        findViewById(R.id.bt_blue_end_chair).setOnClickListener(this);
        findViewById(R.id.bt_blue_end_usb).setOnClickListener(this);
        findViewById(R.id.bt_wifi_start_chair).setOnClickListener(this);
        findViewById(R.id.bt_wifi_start_usb).setOnClickListener(this);
        findViewById(R.id.bt_wifi_end_chair).setOnClickListener(this);
        findViewById(R.id.bt_wifi_end_usb).setOnClickListener(this);
        findViewById(R.id.bt_restart).setOnClickListener(this);
        findViewById(R.id.bt_details).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String chairId = getIntent().getStringExtra("chairId");
        if (TextUtils.isEmpty(chairId)) {
            showToast("设备ID获取失败");
            return;
        }
        mPresenter.requestBindDeviceInfo(chairId);
    }

    @Override
    public void onClick(View v) {
        if (StringUtils.isFastClick()) {
            KLog.w("连击了。。。");
            return;
        }
        switch (v.getId()) {
            case R.id.bt_blue_start_chair:
                KLog.w("正常点击。。。");
                mPresenter.bluetoothChairAction(BlueToothConstants.BLE_START_CHAIR);
                break;
            case R.id.bt_blue_end_chair:
                mPresenter.bluetoothChairAction(BlueToothConstants.BLE_END_CHAIR);
                break;
            case R.id.bt_blue_start_usb:
                mPresenter.bluetoothChairAction(BlueToothConstants.BLE_START_USB);
                break;
            case R.id.bt_blue_end_usb:
                mPresenter.bluetoothChairAction(BlueToothConstants.BLE_END_USB);
                break;
            case R.id.bt_wifi_start_chair:
                mPresenter.wifiActionDevice(BlueToothConstants.WIFI_START_CHAIR);
                break;
            case R.id.bt_wifi_end_chair:
                mPresenter.wifiActionDevice(BlueToothConstants.WIFI_END_CHAIR);
                break;
            case R.id.bt_wifi_start_usb:
                mPresenter.wifiActionDevice(BlueToothConstants.WIFI_START_USB);
                break;
            case R.id.bt_wifi_end_usb:
                mPresenter.wifiActionDevice(BlueToothConstants.WIFI_END_USB);
                break;
            case R.id.bt_restart:
                mPresenter.bluetoothChairAction(BlueToothConstants.BLE_RESTART);
                break;
            case R.id.tv_scan:
                mPresenter.scanPermissions();
                break;
            case R.id.bt_details:
                //查看详情
                Intent intent = new Intent(ActionActivity.this, DetailsActivity.class);
                intent.putParcelableArrayListExtra("bleDataList", mList);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private String showState(String state) {
        if ("0".equals(state)) {
            return "待生产";
        } else if ("1".equals(state)) {
            return "已投放";
        } else if ("2".equals(state)) {
            return "暂停";
        } else if ("7".equals(state)) {
            return "待验收";
        } else if ("10".equals(state)) {
            return "故障";
        }
        return "未知";
    }

    @Override
    protected ActionPresenter createPresenter() {
        return new ActionPresenter(this);
    }


    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(this, msg, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onlineState(String state) {
        tvOnline.setText(getString(R.string.device_online, BlueToothConstants.ONLINE.equals(state) ? "在线" : "离线"));
    }

    @Override
    public int getWriteTime() {
        int time;
        String str = etWriteTime.getText().toString().trim();
        if (!TextUtils.isEmpty(str)) {
            time = Integer.parseInt(str);
        } else {
            time = 500;
        }
        KLog.d("读写间隔: " + time);
        return time;
    }

    @Override
    public void initTextView(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean != null) {
            tvDeviceId.setText(getString(R.string.device_id, deviceInfoBean.getDevice_id()));
            tvAddress.setText(getString(R.string.device_addr, deviceInfoBean.getSite_name()));
            tvState.setText(getString(R.string.device_launch_state, showState(deviceInfoBean.getStatus())));
        }
    }

    @Override
    public void bleList(ArrayList<BleDevice> list) {
        mList = list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, data);
    }
}
