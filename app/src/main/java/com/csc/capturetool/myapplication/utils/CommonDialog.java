package com.csc.capturetool.myapplication.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.csc.capturetool.myapplication.R;


/**
 * Created by yangkun on 2018/5/25.
 */

public class CommonDialog extends Dialog implements View.OnClickListener {
    private TextView mTitle;
    private TextView mContent;
    private TextView mCancel;
    private TextView mConfirm;

    private String title;
    private String content;

    private CommonDialogListener listener;
    private View mDivider;
    private boolean isCancelVisible;
    private boolean isConfirmVisible;

    public interface CommonDialogListener {
        void confirm(View view);

        void cancel(View view);
    }

    public CommonDialog(@NonNull Context context) {
        this(context, R.style.Theme_NoTitleBarDialog);
    }

    public CommonDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public CommonDialog(@NonNull Context context, String title, String content) {
        super(context, R.style.Theme_NoTitleBarDialog);
        this.title = title;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        mTitle = findViewById(R.id.tv_title);
        mContent = findViewById(R.id.tv_content);
        mCancel = findViewById(R.id.tv_cancel);
        mConfirm = findViewById(R.id.tv_confirm);

        mDivider = findViewById(R.id.ll_vertical_divider);

        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }

        if (!TextUtils.isEmpty(content)) {
            mContent.setText(content);
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTitleVisible(int visible) {
        mTitle.setVisibility(visible);
    }

    public void setContentVisible(int visible) {
        mContent.setVisibility(visible);
    }

    public void setContent(String content) {
        mContent.setText(content);
    }

    public void setCancelVisible(int visible) {
        isCancelVisible = visible == View.VISIBLE;
        if (isCancelVisible && isConfirmVisible) {
            mDivider.setVisibility(View.VISIBLE);
        } else {
            mDivider.setVisibility(View.GONE);
        }
        mCancel.setVisibility(visible);
    }

    public void setConfirmVisible(int visible) {
        isConfirmVisible = visible == View.VISIBLE;
        if (isCancelVisible && isConfirmVisible) {
            mDivider.setVisibility(View.VISIBLE);
        } else {
            mDivider.setVisibility(View.GONE);
        }
        mConfirm.setVisibility(visible);
    }

    public void setCancelText(String btnName) {
        mCancel.setText(btnName);
    }

    public void setCancelTextSize(int size) {
        mCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setCancelTextColor(int color) {
        mCancel.setTextColor(color);
    }

    public void setConfirmText(String btnName) {
        mConfirm.setText(btnName);
    }

    public void setOnClickListener(CommonDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (view == mCancel) {
            if (listener != null) {
                listener.cancel(mCancel);
                dismiss();
            }
        } else if (view == mConfirm) {
            if (listener != null) {
                listener.confirm(mConfirm);
                dismiss();
            }
        }
    }
}
