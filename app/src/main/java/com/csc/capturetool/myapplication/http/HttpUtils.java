package com.csc.capturetool.myapplication.http;

import android.content.Context;

import com.csc.capturetool.myapplication.CscApplication;
import com.csc.capturetool.myapplication.http.api.CscApi;
import com.csc.capturetool.myapplication.http.listener.OnResultListener;
import com.csc.capturetool.myapplication.http.observer.DataObserver;
import com.csc.capturetool.myapplication.modules.action.model.AliveBean;
import com.csc.capturetool.myapplication.modules.action.model.BlueToothDataBean;
import com.csc.capturetool.myapplication.modules.action.model.DeviceInfoBean;
import com.csc.capturetool.myapplication.modules.action.model.GcdecodeBean;
import com.csc.capturetool.myapplication.utils.MapUtils;
import com.csc.capturetool.myapplication.utils.StringUtils;
import com.socks.library.KLog;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public class HttpUtils {
    private static HttpUtils sHttpUtils;
    private static HttpHelper mHttpHelper;
    public static MapUtils paramMap = MapUtils.getInstance();

    public static HttpUtils getInstance() {
        Map<String, String> paramsMapGet = StringUtils.paramsMapBasic(paramMap.getMap());
        CommonParamsInterceptor interceptor = new CommonParamsInterceptor.Builder()
                //get request,add query params to request url
                .addQueryParamsMap(paramsMapGet)
                .build();
        mHttpHelper = new HttpHelper(CscApplication.getApplication(), interceptor);

        if (sHttpUtils == null) {
            synchronized (HttpUtils.class) {
                if (sHttpUtils == null) {
                    sHttpUtils = new HttpUtils();
                }
            }
        }
        return sHttpUtils;
    }


    /**
     * 登录
     *
     * @param context
     * @param phoneNum
     * @param pwd
     * @param onResultListener
     */
    public void doLogin(Context context, String phoneNum, String pwd, OnResultListener<HttpResult<String>> onResultListener) {
        CscApi haChiApi = mHttpHelper.getService(CscApi.class, BaseUrls.CSC_BASE_URL);
        haChiApi.doLogin(phoneNum, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DataObserver<>(context, onResultListener));
    }

    /**
     * 检测设备是否在线
     */
    public void alive(Context context, String devid, OnResultListener<HttpResult<AliveBean>> onResultListener) {
        paramMap.put("m", HttpConstants.ALIVE);
        paramMap.put("devid", devid);
        String sign = StringUtils.getSignParam(paramMap.getMap());
        CscApi haChiApi = mHttpHelper.getService(CscApi.class, BaseUrls.CSC_BASE_URL);
        haChiApi.alive(devid, HttpConstants.ALIVE, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DataObserver<>(context, onResultListener));

    }

    /**
     * 获取设备id信息
     *
     * @param context
     * @param onResultListener
     */
    public void getPositionInfo(Context context, String devid, OnResultListener<HttpResult<DeviceInfoBean>> onResultListener) {
        paramMap.put("m", HttpConstants.XCNINFO);
        paramMap.put("devid", devid);
        String sign = StringUtils.getSignParam(paramMap.getMap());
        CscApi haChiApi = mHttpHelper.getService(CscApi.class, BaseUrls.CSC_BASE_URL);
        haChiApi.getPositionInfo(devid, HttpConstants.XCNINFO, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DataObserver<>(context, onResultListener));
    }


    public void getCmd(Context context, String devid, int ctype, int cvalue, OnResultListener<BlueToothDataBean> onResultListener) {
        paramMap.put("m", HttpConstants.GETCMD);
        paramMap.put("macid", devid);
        paramMap.put("ctype", String.valueOf(ctype));
        paramMap.put("cvalue", String.valueOf(cvalue));
        String sign = StringUtils.getSignParam(paramMap.getMap());
        //        增加gson过滤器
        //        CscApi haChiApi = mHttpHelper.getService(CscApi.class, BaseUrls.CSC_BASE_URL, GsonBuilder.buildGson());
        CscApi haChiApi = mHttpHelper.getService(CscApi.class, BaseUrls.CSC_BASE_URL);
        haChiApi.getCmd(devid, ctype, cvalue, HttpConstants.GETCMD, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DataObserver<>(context, onResultListener));
    }

    public void gcdecode(Context context, String data, OnResultListener<HttpResult<GcdecodeBean>> onResultListener) {
        paramMap.put("m", HttpConstants.GCDECODE);
        paramMap.put("data", data);
        String sign = StringUtils.getSignParam(paramMap.getMap());
        CscApi haChiApi = mHttpHelper.getService(CscApi.class, BaseUrls.CSC_BASE_URL);
        haChiApi.gcdecode(data, HttpConstants.GCDECODE, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DataObserver<>(context, onResultListener));
    }

    /**
     * wifi操作设备
     *
     * @param context
     * @param macid
     * @param tp
     * @param onResultListener
     */
    public void gccontrol(Context context, String macid, String tp, OnResultListener<HttpResult<Object>> onResultListener) {
        paramMap.put("m", HttpConstants.GCCONTROL);
        paramMap.put("macid", macid);
        paramMap.put("tp", tp);
        String sign = StringUtils.getSignParam(paramMap.getMap());
        CscApi haChiApi = mHttpHelper.getService(CscApi.class, BaseUrls.CSC_BASE_URL);
        haChiApi.gccontrol(macid, tp, HttpConstants.GCCONTROL, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DataObserver<>(context, onResultListener));

    }

}
