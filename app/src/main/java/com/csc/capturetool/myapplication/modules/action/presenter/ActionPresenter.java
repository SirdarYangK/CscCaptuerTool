package com.csc.capturetool.myapplication.modules.action.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;
import com.csc.capturetool.myapplication.R;
import com.csc.capturetool.myapplication.base.BasePresenter;
import com.csc.capturetool.myapplication.constansts.BlueToothConstants;
import com.csc.capturetool.myapplication.http.HttpResult;
import com.csc.capturetool.myapplication.http.HttpUtils;
import com.csc.capturetool.myapplication.http.listener.OnResultListener;
import com.csc.capturetool.myapplication.modules.action.model.AliveBean;
import com.csc.capturetool.myapplication.modules.action.model.BlueToothDataBean;
import com.csc.capturetool.myapplication.modules.action.model.DeviceInfoBean;
import com.csc.capturetool.myapplication.modules.action.model.GcdecodeBean;
import com.csc.capturetool.myapplication.modules.action.view.IActionView;
import com.csc.capturetool.myapplication.modules.action.view.activity.ActionActivity;
import com.csc.capturetool.myapplication.modules.home.MainActivity;
import com.csc.capturetool.myapplication.service.bluetooth.BluetoothService;
import com.csc.capturetool.myapplication.utils.MapUtils;
import com.csc.capturetool.myapplication.utils.RxPermissionsUtils;
import com.csc.capturetool.myapplication.utils.ToastUtils;
import com.geek.thread.GeekThreadPools;
import com.socks.library.KLog;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by SirdarYangK on 2018/11/5
 * des:
 */
public class ActionPresenter extends BasePresenter<IActionView> {
    private final static int READDATA = 10001;
    private final static int GCDECODE = 10002;//解析蓝牙返回数据
    private final static int STARTUPDEVICE = 10003;//启动设备
    private final static String UUIDSERVICE = "0000fee7-0000-1000-8000-00805f9b34fb";
    private final static String UUIDNOTIFY = "0000fec5-0000-1000-8000-00805f9b34fb";
    private final static String UUIDWRITE = "0000fec6-0000-1000-8000-00805f9b34fb";
    private final static int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private final static int REQUEST_CODE_OPEN_GPS = 1;
    private ActionActivity mContext;
    private BluetoothService.BluetoothBinder mBluetoothBinder = null;
    private DeviceInfoBean mDeviceInfoBean;
    private BleDevice mBleDevice;
    private BluetoothGatt mBluetoothGatt;
    private int index = 0;
    private int dataNums;//分包发送的包个数
    private StringBuilder stringBuilder;
    private String deviceResultData;//设备返回的数据
    private int deviceActionType;//操作设备动作类型
    private String macAdd;
    private String mDeviceId;
    private String udpsig;
    boolean isFinish = false;//蓝牙数据包是否发送完成
    private int writeTime;
    ArrayList<String> strArr = new ArrayList<>();
    private int countSend = 0;//蓝牙发送数据失败，重发的次数
    private int sumSend = 2;//重发总数

    public ActionPresenter(ActionActivity mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case READDATA:
                    if (isFinish) {
                        isFinish = false;
                    } else {
                        countSend += 1;
                        //重发三次
                        if (countSend <= sumSend) {
                            //                            mView.showToast("第 "+countSend+" 重发");
                            KLog.d("send_count", "第 " + countSend + " 重发");
                            openNotify(strArr);
                        } else {
                            countSend = 0;
                            cancelCmd(true, "No packets received!  Try reconnecting");
                        }
                    }
                    break;
                case GCDECODE:
                    //解析蓝牙返回数据
                    String strData = (String) msg.obj;
                    requestGcdecode(strData);
                    break;

                case STARTUPDEVICE:
                    startUPDevice();
                    break;
                default:
                    break;
            }
        }
    };

    public void initData(DeviceInfoBean deviceInfoBean) {
        this.mDeviceInfoBean = deviceInfoBean;
        mDeviceId = mDeviceInfoBean.getDevice_id();
        mView.initTextView(deviceInfoBean);
        String regex = "(.{2})";
        macAdd = mDeviceInfoBean.getMac_id().replaceAll(regex, "$1:");
        if (macAdd.endsWith(":")) {
            macAdd = macAdd.substring(0, macAdd.length() - 1);
        }
        KLog.w("mac地址：" + macAdd + "   lenght: " + macAdd.length());
        checkDeviceOnline();
        setScanRule();
    }

    /**
     * 设置蓝牙初始值
     */
    private void setScanRule() {
        UUID[] uuid = new UUID[1];
        uuid[0] = UUID.fromString(UUIDSERVICE);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(uuid)      // 只扫描指定的服务的设备，可选
                //                .setDeviceName(true, names)         // 只扫描指定广播名的设备，可选
                .setDeviceMac(macAdd)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(15000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        startScan();
        //        GeekThreadPools.executeWithGeekThreadPool(new Runnable() {
        //            @Override
        //            public void run() {
        //                //                扫描
        //                startScan();
        //                KLog.d("setScanRule  Thread: " + Thread.currentThread().getName());
        //            }
        //        });
    }

    /**
     * 检测设备是否在线
     */
    public void checkDeviceOnline() {
        HttpUtils.getInstance().alive(mContext, mDeviceId, new OnResultListener<HttpResult<AliveBean>>() {
            @Override
            public void onSuccess(HttpResult<AliveBean> result) {
                KLog.json("检测设备是否在线: " + result);
                if (result.getCode() == 0) {
                    mView.onlineState(result.getData().getLive());
                } else {
                    mView.showToast(result.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                MapUtils.getInstance().clearData();
            }

            @Override
            public void onComplete() {
                MapUtils.getInstance().clearData();
            }
        });
    }

    /**
     * 蓝牙操作椅子
     *
     * @param state 开关状态 1，启动； 0，关闭
     */
    public void bluetoothChairAction(int state) {
        writeTime = mView.getWriteTime();
        deviceActionType = state;
        checkBluetoothValid();
    }

    //     ArrayList<BleDevice> list= new ArrayList<>();
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            // 会回到主线程，参数表示本次扫描动作是否开启成功
            @Override
            public void onScanStarted(boolean success) {
                KLog.d("Scan Started  。。。" + "  thread: " + Thread.currentThread().getName());
            }

            //扫描过程中所有被扫描到的结果回调
            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                //                if (macAdd.equals(bleDevice.getMac())) {
                //                    BleManager.getInstance().cancelScan();
                //                }

                //                KLog.d(bleDevice.getMac());


                //                list.add(bleDevice);
            }

            //扫描过程中的所有过滤后的结果回调。与onLeScan区别之处在于：它会回到主线程；同一个设备只会出现一次；出现的设备是经过扫描过滤规则过滤后的设备。
            @Override
            public void onScanning(BleDevice bleDevice) {
                BleManager.getInstance().cancelScan();
                mBleDevice = bleDevice;
                cancelCmd(false, "Scan Success");
                KLog.d("deviceName   " + bleDevice.getName() + "=======deviceMaac    " + bleDevice.getMac() + " thread: " + Thread.currentThread().getName());

            }

            //本次扫描时段内所有被扫描且过滤后的设备集合。它会回到主线程，相当于onScanning设备之和
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                //                mView.bleList(list);


                KLog.d("scan finish!!!   " + scanResultList.size() + " thread: " + Thread.currentThread().getName());
                if (scanResultList.size() < 1) {
                    cancelCmd(false, "Not scanned to equipment! Possible reasons  MAC id Mismatch or device not open");
                } else {
                    cancelCmd(false, "");
                }
            }
        });
    }

    private void connectDevice(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                KLog.d("Start Connect");
                countDownTimer.start();
                mView.showProgressBar();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException e) {
                KLog.d("Connect Fail");
                cancelCmd(true, "Connect Bluetooth Fail");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                KLog.d("Connect success");
                //                mBleDevice = bleDevice;
                mBluetoothGatt = bluetoothGatt;
                mHandler.sendEmptyMessageDelayed(STARTUPDEVICE, 100);
            }

            @Override
            public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                //                cancelCmd(true,"Disconnect !!!");
                KLog.e("Disconnect");
            }
        });
    }

    /**
     * 启动设备
     */
    int ctype, cvalue;

    private void startUPDevice() {
        switch (deviceActionType) {
            case BlueToothConstants.BLE_START_CHAIR:
                ctype = 10;
                cvalue = 60;
                break;
            case BlueToothConstants.BLE_END_CHAIR:
                ctype = 10;
                cvalue = 0;
                break;
            case BlueToothConstants.BLE_START_USB:
                ctype = 11;
                cvalue = 60;
                break;
            case BlueToothConstants.BLE_END_USB:
                ctype = 11;
                cvalue = 0;
                break;
            case BlueToothConstants.BLE_RESTART:
                ctype = 84;
                cvalue = 0;
                break;
            default:
        }
        KLog.w("mDeviceId:" + mDeviceId + "  ctype:" + ctype + "  cvalue:" + cvalue);
        HttpUtils.getInstance().getCmd(mContext, mDeviceId, ctype, cvalue, new OnResultListener<BlueToothDataBean>() {
            @Override
            public void onSuccess(BlueToothDataBean result) {
                KLog.d("response result：" + result + "   Thread: " + Thread.currentThread());
                if (result.getCode() == 0) {
                    KLog.d(result);
                    udpsig = result.getUdpsig();
                    String data = result.getData1();
                    if (!TextUtils.isEmpty(data)) {
                        GeekThreadPools.executeWithGeekThreadPool(new Runnable() {
                            @Override
                            public void run() {
                                sendData(data);
                                KLog.d("Thread: " + Thread.currentThread().getName());
                            }
                        });
                    }
                } else {
                    cancelCmd(true, "获取蓝牙数据包错误，请检查网络");
                }
            }

            @Override
            public void onError(Throwable e) {
                KLog.e(e);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void sendData(String data) {
        dataNums = data.length() / 32;
        int j = data.length() % 32;
        if (data.length() < 32) {
            dataNums = 1;
        } else if (data.length() > 32 && j != 0) {
            dataNums += 1;
        }
        if (dataNums >= 10) {
            mView.showToast("getcmd data request error");
            return;
        }
        //填充数据前，先清空
        strArr.clear();
        for (int i = 0; i < dataNums; i++) {
            //对最后一个包进行处理
            if (i == dataNums - 1) {
                //最后一个包，数据个数的十六进制形式
                String strHex = Integer.toHexString((data.length() - (i * 32)) / 2);
                strArr.add("51" + "0" + (i + 1) + BlueToothConstants.TYPE_DATA + dataNums + (i + 1) + strHex + data.substring(i * 32, data.length()));
                break;
            }
            strArr.add("51" + "0" + (i + 1) + BlueToothConstants.TYPE_DATA + dataNums + (i + 1) + "0" + data.substring(i * 32, i * 32 + 32));
        }
        dataNums = strArr.size();
        openNotify(strArr);
    }


    public void openNotify(ArrayList<String> datas) {
        stringBuilder = new StringBuilder();
        BleManager.getInstance().notify(
                mBleDevice,
                UUIDSERVICE,
                UUIDNOTIFY,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        // 打开通知操作成功
                        KLog.d("open notify success");
                        index = 0;
                        writeData(datas.get(index));
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        // 打开通知操作失败
                        cancelCmd(true, "open bluetooth notify fail");
                        KLog.d("open bluetooth notify fail");
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                KLog.w("设备应答data:  " + HexUtil.formatHexString(data, true) + "  data_length: " + data.length);
                                String strData = HexUtil.formatHexString(data);
                                index = Integer.parseInt(strData.substring(3, 4));
                                String type = strData.substring(4, 5);
                                if (data.length == 4 && index < dataNums) {
                                    KLog.d("data.length: " + data.length + ";  request_index: " + index + ";  dataNums: " + dataNums);
                                    writeData(datas.get(index));
                                } else if (data.length > 4 && BlueToothConstants.TYPE_DATA.equals(type)
                                        && index <= dataNums) {
                                    isFinish = true;
                                    KLog.d("data.length: " + data.length + ";  response_index: " + index + ";  dataNums: " + dataNums);
                                    //数据包长度大于4，类型是0，包号不大于包总数，才可写入buffer
                                    deviceResultData = stringBuilder.append(strData.substring(8, strData.length())).toString();
                                    KLog.d("device return data:  " + deviceResultData);

                                    if (index == dataNums) {
                                        Message message = mHandler.obtainMessage();
                                        message.what = GCDECODE;
                                        message.obj = deviceResultData;
                                        mHandler.sendMessage(message);
                                        KLog.d("设备交互是否完成：" + isFinish);
                                    }

                                    String substring = strData.substring(0, 8);
                                    StringBuilder sb = new StringBuilder(substring);
                                    sb.replace(4, 5, BlueToothConstants.TYPE_ANSWER);
                                    writeData(sb.toString());
                                } else {
                                    KLog.d("设备交互是否完成：" + isFinish);
                                    mHandler.sendEmptyMessageDelayed(READDATA, 2000);

                                }
                            }
                        }, writeTime);
                    }
                });
    }


    /**
     * 二维码扫描权限
     */
    public void scanPermissions() {
        RxPermissionsUtils.requestPermission(mContext, new RxPermissionsUtils.OnPermissionsGranted() {
                    @Override
                    public void onGranted() {
                        checkscanValid();
                    }

                    @Override
                    public void onDeniend() {

                    }
                }, Manifest.permission.BLUETOOTH
                , Manifest.permission.BLUETOOTH_ADMIN
                , Manifest.permission.CAMERA
                , Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void checkscanValid() {
        //判断当前Android设备是否支持BLE
        boolean supportBle = BleManager.getInstance().isSupportBle();
        //判断当前Android设备的蓝牙是否已经打开
        boolean blueEnable = BleManager.getInstance().isBlueEnable();
        if (!supportBle) {
            AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("错误").setMessage("你的设备不具备蓝牙功能!").create();
            dialog.show();
            return;
        }

        if (!blueEnable) {
            ToastUtils.showToast(mContext, "请先打开蓝牙", 0);
            return;
        }

        //开启位置信息
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(mContext, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(mContext, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示")
                            .setMessage("当前手机扫描蓝牙需要打开定位功能")
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mContext.finish();
                                        }
                                    })
                            .setPositiveButton("前往设置",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            mContext.startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    Intent intent = new Intent(mContext, CaptureActivity.class);
                    mContext.startActivityForResult(intent, 11);
                }
                break;
            default:
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    /**
     * 校验手机是否打开蓝牙
     */
    private void checkBluetoothValid() {
        //判断当前Android设备的蓝牙是否已经打开
        boolean blueEnable = BleManager.getInstance().isBlueEnable();
        if (!blueEnable) {
            //            AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
            //                    .setMessage("蓝牙设备未打开,请开启此功能后重试!")
            //                    .setPositiveButton("确认", (arg0, arg1) -> {
            //                        Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //                        mContext.startActivityForResult(mIntent, 10);
            //                    })
            //                    .create();
            //            dialog.show();
            mView.showToast("请先打卡蓝牙");
            return;
        }
        openBlueTooth();

    }

    /**
     * 打开蓝牙
     */
    private void openBlueTooth() {
        if (!BleManager.getInstance().isConnected(mBleDevice)) {
            KLog.d("bluetooth 未连接");
            //BleManager.getInstance().cancelScan();
            connectDevice(mBleDevice);
        } else {
            //若是连接状态，直接发送命令
            KLog.d("bluetooth 已连接");
            mHandler.sendEmptyMessageDelayed(STARTUPDEVICE, 100);
        }
    }

    /**
     * 蓝牙写入数据
     */
    private void writeData(String data) {
        KLog.d("要写入的数据：  " + data);
        BleManager.getInstance().write(
                mBleDevice,
                UUIDSERVICE,
                UUIDWRITE,
                HexUtil.hexStringToBytes(data),
                true,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                        KLog.d("write success, current: " + current
                                + " total: " + total
                                + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        KLog.d("write Failure, current: " + exception);
                        cancelCmd(true, "write Failure !!!");
                    }
                });
    }

    public void requestGcdecode(String strData) {
        KLog.w("蓝牙数据包： " + strData);
        HttpUtils.getInstance().gcdecode(mContext, strData, new OnResultListener<HttpResult<GcdecodeBean>>() {
            @Override
            public void onSuccess(HttpResult<GcdecodeBean> result) {
                KLog.json("解析蓝牙返回数据: " + result);
                if (result.getCode() == 0 && result.getData() != null) {
                    GcdecodeBean data = result.getData();

                    String timespan = String.valueOf(data.getTimespan());
                    String radom = String.valueOf(data.getRadom());
                    if (udpsig.equals(timespan + radom)) {
                        String cmdid = data.getCmds().get(0).getCmdid();
                        String cmdctx = data.getCmds().get(0).getCmdctx().substring(0, 2);
                        if (BlueToothConstants.DEVICE_CHAIR.equals(cmdid)) {
                            if (BlueToothConstants.DEVICE_ON.equals(cmdctx)) {
                                cancelCmd(true, "Start up chair");
                            } else if (BlueToothConstants.DEVICE_OFF.equals(cmdctx)) {
                                cancelCmd(true, "Pause chair");
                            }
                        } else if (BlueToothConstants.DEVICE_USB.equals(cmdid)) {
                            if (BlueToothConstants.DEVICE_ON.equals(cmdctx)) {
                                cancelCmd(true, "Start up USB");
                            } else if (BlueToothConstants.DEVICE_OFF.equals(cmdctx)) {
                                cancelCmd(true, "Pause USB");
                            }
                        } else {
                            cancelCmd(true, "Success");
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                //                MapUtils.getInstance().clearData();
            }

            @Override
            public void onComplete() {
                //                MapUtils.getInstance().clearData();
            }
        });
    }

    private CountDownTimer countDownTimer = new CountDownTimer(20000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            cancelCmd(true, "connection timed out");
        }
    };

    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                Intent intent = new Intent(mContext, CaptureActivity.class);
                mContext.startActivityForResult(intent, 11);
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
                    requestBindDeviceInfo(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    mView.showToast("QR code parsing failure !!!");
                }
            }
        }
    }

    /**
     * 根据椅子id进行绑定
     *
     * @param chairId 椅子Id
     */
    public void requestBindDeviceInfo(String chairId) {
        mView.showProgressBar();
        countDownTimer.start();
        chairId = chairId.substring(chairId.length() - 11);
        KLog.d("从action扫描结果: " + chairId);
        HttpUtils.getInstance().getPositionInfo(mContext, chairId, new OnResultListener<HttpResult<DeviceInfoBean>>() {
            @Override
            public void onSuccess(HttpResult<DeviceInfoBean> result) {
                KLog.json("通过deviceId获取的后台数据: " + result);
                if (result.getCode() == 0 && result.getData() != null) {
                    initData(result.getData());
                } else {
                    mView.showToast(result.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                //                MapUtils.getInstance().clearData();
                cancelCmd(true, "网络异常，请检查网络");
                KLog.e(e);
            }

            @Override
            public void onComplete() {
                //                MapUtils.getInstance().clearData();
                //                cancelCmd(false, "");
            }
        });
    }

    public void cancelCmd(boolean isConnect, String tostMsg) {
        if (isConnect && BleManager.getInstance().isConnected(mBleDevice)) {
            BleManager.getInstance().disconnect(mBleDevice);
        }
        mView.dismissProgressBar();
        countDownTimer.cancel();
        if (!TextUtils.isEmpty(tostMsg)) {
            mView.showToast(tostMsg);
        }
    }

    /**
     * wifi操作
     */
    public void wifiActionDevice(String type) {
        mView.showProgressBar();
        countDownTimer.start();
        KLog.d("wifiparam======  mDeviceId: " + mDeviceId + "   state:  " + type);
        HttpUtils.getInstance().gccontrol(mContext, mDeviceId, type, new OnResultListener<HttpResult<Object>>() {
            @Override
            public void onSuccess(HttpResult<Object> result) {
                KLog.d("wifi result: " + result);
                if (result.getCode() == 0) {
                    mView.showToast(showMsg(type));
                }
            }

            @Override
            public void onError(Throwable e) {
                cancelCmd(false, "");
                //                MapUtils.getInstance().clearData();
            }

            @Override
            public void onComplete() {
                cancelCmd(false, "");
                //                MapUtils.getInstance().clearData();
            }
        });
    }

    private String showMsg(String type) {
        if (BlueToothConstants.WIFI_START_CHAIR.equals(type)) {
            return "Start up chair";
        } else if (BlueToothConstants.WIFI_END_CHAIR.equals(type)) {
            return "Pause chair";
        } else if (BlueToothConstants.WIFI_START_USB.equals(type)) {
            return "Start up USB";
        } else if (BlueToothConstants.WIFI_END_USB.equals(type)) {
            return "Pause USB";
        } else {
            return "";
        }

    }

    public void onDestroy() {
        cancelCmd(true, "");
    }
}
