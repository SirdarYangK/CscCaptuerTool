package com.csc.capturetool.myapplication.base;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.csc.capturetool.myapplication.http.listener.LifeCycleObserver;
import com.csc.capturetool.myapplication.utils.DialogUtils;
import com.csc.capturetool.myapplication.utils.SystemBarTintUtils;

public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {

    protected T mPresenter;

    private static final int STATUS_BAR_COLOR = Color.parseColor("#f8f8f8");
    private boolean isShowing;
    private LifeCycleObserver observer;
    private Dialog mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentView());
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
        int mode = getStatusBarLightMode();
        switch (mode) {
            case 1:
            case 2:
            case 3:
                SystemBarTintUtils.setStatusBarColor(this, STATUS_BAR_COLOR);
                break;
            case 0:
                SystemBarTintUtils.setStatusBarColor(this, Color.parseColor("#000000"));
                break;
            default:
        }
        initView();
        initData();
    }

    protected int getStatusBarLightMode() {
        return SystemBarTintUtils.getStatusBarLightMode(this);
    }

    protected abstract int getContentView();

    protected abstract void initView();

    protected abstract void initData();

    //    @Override
    //    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //    }

    protected abstract T createPresenter();

    public void showProgressBar() {
        if (!isShowing) {
            mProgressBar = DialogUtils.showIosLoading(this, new DialogUtils.OnKeyBackListener() {
                @Override
                public void onKeyBack() {
                    onBackPressed();
                }
            });
            isShowing = true;
        }
    }

    public void setActivityLifeCycleObserver(LifeCycleObserver observer) {
        this.observer = observer;
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressBar();
    }

    public void dismissProgressBar() {
        if (isShowing) {
            if (mProgressBar != null) {
                mProgressBar.dismiss();
                mProgressBar = null;
            }
            isShowing = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            observer.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        dismissProgressBar();
        super.onBackPressed();
    }
}
