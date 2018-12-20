package com.csc.capturetool.myapplication.http.listener;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public interface OnResultListener<T> {
    void onSuccess(T t);
    void onError(Throwable e);
    void onComplete();
}