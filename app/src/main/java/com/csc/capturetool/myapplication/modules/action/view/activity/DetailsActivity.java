package com.csc.capturetool.myapplication.modules.action.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.csc.capturetool.myapplication.R;
import com.csc.capturetool.myapplication.modules.action.adapter.DeviceAdapter;
import com.csc.capturetool.myapplication.modules.home.MainActivity;
import com.socks.library.KLog;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private DeviceAdapter mDeviceAdapter;
    private ArrayList<BleDevice> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mList = getIntent().getParcelableArrayListExtra("bleDataList");
        initView();
    }

    private void initView() {
        mDeviceAdapter = new DeviceAdapter(this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {

            @Override
            public void onDetail(BleDevice bleDevice) {
//                if (BleManager.getInstance().isConnected(bleDevice)) {
//                    Intent intent = new Intent(DetailsActivity.this, OperationActivity.class);
//                    intent.putExtra(OperationActivity.KEY_DATA, bleDevice);
//                    startActivity(intent);
//                }
            }
        });

        ListView listView_device = (ListView) findViewById(R.id.list_device);
        listView_device.setAdapter(mDeviceAdapter);
        for (BleDevice bleDevice : mList) {
            mDeviceAdapter.addDevice(bleDevice);
        }
        mDeviceAdapter.notifyDataSetChanged();
    }
}
