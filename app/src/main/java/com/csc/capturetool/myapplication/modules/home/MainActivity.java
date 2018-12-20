package com.csc.capturetool.myapplication.modules.home;

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
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.csc.capturetool.myapplication.CscApplication;
import com.csc.capturetool.myapplication.R;
import com.csc.capturetool.myapplication.base.BaseActivity;
import com.csc.capturetool.myapplication.base.BasePresenter;
import com.csc.capturetool.myapplication.http.HttpResult;
import com.csc.capturetool.myapplication.http.HttpUtils;
import com.csc.capturetool.myapplication.http.listener.OnResultListener;
import com.csc.capturetool.myapplication.modules.action.view.activity.ActionActivity;
import com.csc.capturetool.myapplication.modules.action.model.DeviceInfoBean;
import com.csc.capturetool.myapplication.utils.MapUtils;
import com.csc.capturetool.myapplication.utils.RxPermissionsUtils;
import com.csc.capturetool.myapplication.utils.ToastUtils;
import com.socks.library.KLog;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView tvTitle;
    private final static int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private final static int REQUEST_CODE_OPEN_GPS = 1;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.app_name));
        findViewById(R.id.bt_scan_qr_code).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        //初始化蓝牙
        BleManager.getInstance().init(CscApplication.getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setSplitWriteNum(20)
                .setConnectOverTime(15000)
                .setReConnectCount(1, 5000)
                .setOperateTimeout(5000);

    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_scan_qr_code:
                openBluetoothPermissions();
                break;

            default:
                break;
        }
    }

    /**
     * 校验手机是否有蓝牙权限
     */
    public void openBluetoothPermissions() {
        RxPermissionsUtils.requestPermission(this, new RxPermissionsUtils.OnPermissionsGranted() {
                    @Override
                    public void onGranted() {
                        checkBluetoothValid();
                    }

                    @Override
                    public void onDeniend() {
                        Log.d("", "");
                    }
                }
                , Manifest.permission.CAMERA
                , Manifest.permission.BLUETOOTH
                , Manifest.permission.BLUETOOTH_ADMIN
                , Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    /**
     * 校验手机是否打开蓝牙
     */
    private void checkBluetoothValid() {
        //判断当前Android设备是否支持BLE
        boolean supportBle = BleManager.getInstance().isSupportBle();
        //判断当前Android设备的蓝牙是否已经打开
        boolean blueEnable = BleManager.getInstance().isBlueEnable();
        if (!supportBle) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("错误").setMessage("你的设备不具备蓝牙功能!").create();
            dialog.show();
            return;
        }

        if (!blueEnable) {
//            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")
//                    .setMessage("蓝牙设备未打开,请开启此功能后重试!")
//                    .setPositiveButton("确认", (arg0, arg1) -> {
//                        Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(mIntent, 10);
//                    })
//                    .create();
//            dialog.show();
            ToastUtils.showToast(this,"请先打开蓝牙",0);
            return;
        }

        //开启位置信息
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("当前手机扫描蓝牙需要打开定位功能")
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton("前往设置",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, 11);
                }
                break;
            default:
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null){
            return false;
        }
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
//            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
//            startActivityForResult(intent, 11);
        }
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 11);
            }
        }
        if (requestCode == 11) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Intent intent = new Intent(MainActivity.this, ActionActivity.class);
                    intent.putExtra("chairId", result);
                    startActivity(intent);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtils.showToastShort(this, "QR code parsing failure !!!");
                }
            }
        }
    }
}
