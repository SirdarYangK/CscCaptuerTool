package com.csc.capturetool.myapplication.http.observer;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.MalformedJsonException;
import android.widget.Toast;

import com.csc.capturetool.myapplication.CscApplication;
import com.csc.capturetool.myapplication.R;
import com.csc.capturetool.myapplication.base.BaseActivity;
import com.csc.capturetool.myapplication.base.BaseFragment;
import com.csc.capturetool.myapplication.http.listener.LifeCycleObserver;
import com.csc.capturetool.myapplication.http.listener.OnResultListener;
import com.csc.capturetool.myapplication.utils.MapUtils;
import com.csc.capturetool.myapplication.utils.NetworkUtils;
import com.csc.capturetool.myapplication.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public class DataObserver<T> implements Observer<T>, LifeCycleObserver {

    private static final String TAG = "DataObserver";
    private Context mContext;
    private Fragment mFragment;

    private OnResultListener mOnResultListener;

    private Disposable mDisposable;

    //Activity 调用此构造函数
    public DataObserver(Context context, OnResultListener onResultListener) {
        this.mContext = context;
        this.mOnResultListener = onResultListener;
    }

    //Fragment 调用此构造函数
    public DataObserver(Fragment fragment, OnResultListener onResultListener) {
        this.mFragment = fragment;
        this.mOnResultListener = onResultListener;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        mDisposable = d;
        if (mContext != null && mContext instanceof Activity) {
            BaseActivity activity = (BaseActivity) mContext;
            activity.setActivityLifeCycleObserver(this);
        }

        if (mFragment != null && mFragment instanceof BaseFragment) {
            BaseFragment fragment = (BaseFragment) mFragment;
            fragment.setFragmentLifeCycleObserver(this);
        }
    }

    @Override
    public void onNext(@NonNull T t) {
        MapUtils.getInstance().clearData();
        mOnResultListener.onSuccess(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        MapUtils.getInstance().clearData();
        mOnResultListener.onError(e);
        handlerError(e);
    }

    @Override
    public void onComplete() {
        MapUtils.getInstance().clearData();
        mOnResultListener.onComplete();
    }

    private void handlerError(Throwable e) {


        if (NetworkUtils.isNetworkAvailable(CscApplication.getApplication())) {
            if (e instanceof SocketTimeoutException) {
                ToastUtils.showToast(CscApplication.getApplication(), R.string.network_time_out, Toast.LENGTH_SHORT);
            } else if (e instanceof ConnectException) {
                ToastUtils.showToast(CscApplication.getApplication(), R.string.network_api_error, Toast.LENGTH_SHORT);
            } else if (e instanceof MalformedJsonException) {//返回json数据格式出错
                ToastUtils.showToast(CscApplication.getApplication(), R.string.data_error, Toast.LENGTH_SHORT);
            } else if (e instanceof JSONException) {//json解析出错
                ToastUtils.showToast(CscApplication.getApplication(), R.string.parse_data_error, Toast.LENGTH_SHORT);
            } else if (e instanceof NumberFormatException) {
                ToastUtils.showToast(CscApplication.getApplication(), R.string.number_format_error, Toast.LENGTH_SHORT);
            } else {
                KLog.i(TAG, "onError" + e.getMessage());
            }
        } else {
            ToastUtils.showToast(CscApplication.getApplication(), R.string.network_error, Toast.LENGTH_SHORT);
            ToastUtils.showToast(CscApplication.getApplication(), R.string.network_error, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
