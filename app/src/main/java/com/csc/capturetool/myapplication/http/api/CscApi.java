package com.csc.capturetool.myapplication.http.api;

import com.csc.capturetool.myapplication.http.HttpResult;
import com.csc.capturetool.myapplication.modules.action.model.AliveBean;
import com.csc.capturetool.myapplication.modules.action.model.BlueToothDataBean;
import com.csc.capturetool.myapplication.modules.action.model.DeviceInfoBean;
import com.csc.capturetool.myapplication.modules.action.model.GcdecodeBean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public interface CscApi {
    /**
     * 登录
     *
     * @param phoneNum 账号
     * @param password 密码
     * @return
     */
    @POST("后台地址")
    @FormUrlEncoded
    Observable<HttpResult<String>> doLogin(@Field("phone") String phoneNum,
                                           @Field("pwd") String password);

    //    @POST("act")
    //    @FormUrlEncoded
    //    Observable<HttpResult<DeviceInfoBean>> getPositionInfo(@Field("") String chairId);
    @GET("act")
    Observable<HttpResult<AliveBean>> alive(@Query("devid") String devid,
                                            @Query("m") String m,
                                            @Query("sign") String sign);

    @GET("act")
    Observable<HttpResult<DeviceInfoBean>> getPositionInfo(@Query("devid") String devid,
                                                           @Query("m") String m,
                                                           @Query("sign") String sign);

    @GET("act")
    Observable<BlueToothDataBean> getCmd(@Query("macid") String macid,
                                         @Query("ctype") int ctype,
                                         @Query("cvalue") int cvalue,
                                         @Query("m") String m,
                                         @Query("sign") String sign);

    @GET("act")
    Observable<HttpResult<GcdecodeBean>> gcdecode(@Query("data") String data,
                                                  @Query("m") String m,
                                                  @Query("sign") String sign);

    @GET("act")
    Observable<HttpResult<Object>> gccontrol(@Query("macid") String data,
                                             @Query("tp") String tp,
                                             @Query("m") String m,
                                             @Query("sign") String sign);

}
