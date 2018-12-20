package com.csc.capturetool.myapplication.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.csc.capturetool.myapplication.http.listener.LifeCycleObserver;
import com.csc.capturetool.myapplication.utils.DialogUtils;
import com.csc.capturetool.myapplication.utils.PermissionUtils;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public abstract class BaseFragment<V, T extends BasePresenter<V>> extends Fragment {
    protected T mPresenter;
    private static final String STATE_SAVE_IS_HIDDEN = "state_save_is_hidden";
    private LifeCycleObserver observer;
    private Dialog mIosLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }

        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    protected abstract T createPresenter();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setFragmentLifeCycleObserver(LifeCycleObserver observer) {
        this.observer = observer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            observer.onDestroy();
        }
    }

    public void showProgressBar() {
        if (mIosLoading == null || !mIosLoading.isShowing()) {
            mIosLoading = DialogUtils.showIosLoading(getContext(), new DialogUtils.OnKeyBackListener() {
                @Override
                public void onKeyBack() {

                }
            });
        }
    }

    public void dismissProgressBar() {
        if (mIosLoading != null && mIosLoading.isShowing()) {
            mIosLoading.dismiss();
        }
    }
}
