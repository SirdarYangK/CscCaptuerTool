package com.csc.capturetool.myapplication.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import com.socks.library.KLog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * @author yangkun
 * @time 2018/7/16
 */

public class RxPermissionsUtils {
    private static final String TAG = "RxPermissionsUtils";

    /**
     * 申请权限结果监听
     */
    public interface OnPermissionsGranted {
        /**
         * 权限请求同意
         */
        void onGranted();

        /**
         * 权限被拒绝
         */
        void onDeniend();
    }

    @SuppressLint("CheckResult")
    public static void requestPermission(final Activity activity,
                                         final OnPermissionsGranted onPermissionsGranted,
                                         String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        new RxPermissions(activity)
                .request(permissions)
                .subscribe(new Consumer<Boolean>() {

                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            KLog.d(TAG, "accept:  同意");
                            if (onPermissionsGranted != null) {
                                KLog.d(TAG, "accept: 走了回调");
                                onPermissionsGranted.onGranted();
                            }
                        } else {
                            KLog.d(TAG, "accept: 勾选不在提示");
                            CommonDialog commonDialog = new CommonDialog(activity, "",
                                    "权限被拒绝，将导致APP无法正常使用，请前往设置中修改");
                            commonDialog.show();
                            commonDialog.setTitleVisible(View.GONE);
                            commonDialog.setCancelText("取消");
                            commonDialog.setConfirmText("前往");
                            commonDialog.setOnClickListener(new CommonDialog.CommonDialogListener() {
                                @Override
                                public void confirm(View view) {
                                    startAppSettings(activity);
                                }

                                @Override
                                public void cancel(View view) {
                                    if (onPermissionsGranted != null) {
                                        KLog.d(TAG, "accept: 走了回调");
                                        onPermissionsGranted.onDeniend();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    /**
     * 启动当前应用设置页面
     */
    private static void startAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
