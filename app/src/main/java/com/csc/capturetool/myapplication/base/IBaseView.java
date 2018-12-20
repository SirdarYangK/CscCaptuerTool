package com.csc.capturetool.myapplication.base;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public interface IBaseView {
    /**
     * toast 提示
     *
     * @param msg 要提示的信息
     */
    void showToast(String msg);

    /**
     * 展示loading
     */
    void showProgressBar();

    /**
     * 隐藏loading
     */
    void dismissProgressBar();
}
