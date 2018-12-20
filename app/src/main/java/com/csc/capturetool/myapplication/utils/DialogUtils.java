package com.csc.capturetool.myapplication.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csc.capturetool.myapplication.R;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public class DialogUtils {
    private static DialogUtils instance;
    private DialogUtils() {

    }

    public static DialogUtils getInstance() {
        if (instance == null) {
            instance = new DialogUtils();
        }
        return instance;
    }
    /**
     * 仿ios菊花效果的loading
     *
     * @param context
     */
    public static Dialog showIosLoading(Context context, final DialogUtils.OnKeyBackListener onKeyBackListener) {
        if (context == null) {
            return null;
        }
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int) (100 * density), (int) (100 * density));
        final Dialog dialog = new Dialog(context, R.style.Theme_NoTitleBarDialog);
        View view = View.inflate(context, R.layout.ios_loading_layout, null);
        TextView loadingText = view.findViewById(R.id.tv_loading_text);
        dialog.setContentView(view, params);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    //点击了返回键
                    if (onKeyBackListener != null) {
                        onKeyBackListener.onKeyBack();
                        return true;
                    }
                }
                return false;
            }
        });
        dialog.show();
        return dialog;
    }

    /**
     * 点击返回按钮的监听
     */
    public interface OnKeyBackListener {
        void onKeyBack();
    }
}
