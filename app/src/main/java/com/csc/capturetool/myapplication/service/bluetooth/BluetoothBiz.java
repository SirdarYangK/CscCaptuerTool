package com.csc.capturetool.myapplication.service.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.csc.capturetool.myapplication.utils.DataFormat;

import java.util.List;
import java.util.UUID;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:蓝牙帮助类
 */

public class BluetoothBiz {

    private final static String TAG = "CSCLAUNCHER";

    private Context mContext = null;

    private BluetoothManager mBluetoothManager = null;

    private BluetoothAdapter mBluetoothAdapter = null;

    private String mBluetoothDeviceAddress = null;

    private BluetoothGatt mBluetoothGatt = null;


    private int mConnectionState = STATE_DISCONNECTED;


    private static final int STATE_DISCONNECTED = 0;

    private static final int STATE_CONNECTING = 1;

    private static final int STATE_CONNECTED = 2;


    public final static String ACTION_GATT_CONNECTED = "com.choicemmed.bledemo.ACTION_GATT_CONNECTED";

    public final static String ACTION_GATT_DISCONNECTED = "com.choicemmed.bledemo.ACTION_GATT_DISCONNECTED";

    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.choicemmed.bledemo.ACTION_GATT_SERVICES_DISCOVERED";

    public final static String ACTION_DATA_AVAILABLE = "com.choicemmed.bledemo.ACTION_DATA_AVAILABLE";

    public final static String EXTRA_DATA = "com.choicemmed.bledemo.EXTRA_DATA";

    public BluetoothBiz(Context mContext) {
        this.mContext = mContext;
        init();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        private final static String TAG = "CSCLAUNCHER";

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            String intentAction = null;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:"
                        + mBluetoothGatt.discoverServices());
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                List<BluetoothGattService> gattServices  = mBluetoothGatt.getServices();
                Log.i(TAG, "service size:  " + gattServices.size());
                for (BluetoothGattService service : gattServices){
                    Log.i(TAG, "UUID:  " + service.getCharacteristics());
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.i(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicWrite:" + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            UUID uuid = descriptor.getCharacteristic().getUuid();
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        }
    };

    /**
     * 初始化蓝牙参数
     */
    public boolean init() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.i(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * 根据mac地址查找蓝牙设备
     *
     * @param address mac地址
     */
    public boolean connectByMacaddress(String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.i(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device. Try to reconnect.
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            Log.i(TAG, "没有设备");
            return false;
        }
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        Log.i(TAG, "address:  " + address + "; mBluetoothGatt:  " + mBluetoothGatt);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }

    /**
     * 将byte数组转换成16进制字符串后发送广播
     * @param action 蓝牙事件
     * @param characteristic 数据载体
     */
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        byte[] bytesValue = characteristic.getValue();
        String data = bytesToHexString(bytesValue);
        if (data != null) {
            intent.putExtra(EXTRA_DATA, data);
        }
        mContext.sendBroadcast(intent);
    }

    /**
     * byte数组转16进制字符串
     *
     * @param src byte数组
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!DataFormat.isEmpty(src)) {
            for (int i = 0; i < src.length; i++) {
                int v = src[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
        }
        return stringBuilder.toString();
    }

    public void disConnect(){
        mBluetoothGatt.disconnect();
    }
}
