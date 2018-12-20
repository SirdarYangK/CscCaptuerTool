package com.csc.capturetool.myapplication.base;

import java.lang.ref.WeakReference;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public class BasePresenter<V> {
    protected WeakReference<V> weak;
    protected V mView;
    public void attachView(V view) {
        weak = new WeakReference<V>(view);
        mView = weak.get();
    }

    public boolean isViewAttached() {
        return mView != null;
    }

    public void detachView() {
        if (mView != null) {
            mView = null;
        }
    }
}
