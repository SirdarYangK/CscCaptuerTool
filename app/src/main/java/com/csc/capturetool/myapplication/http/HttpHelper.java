package com.csc.capturetool.myapplication.http;

import android.content.Context;

import com.csc.capturetool.myapplication.utils.FileUtil;
import com.csc.capturetool.myapplication.utils.NetworkUtils;
import com.google.gson.Gson;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public class HttpHelper {
    private static final String TAG = "HttpHelper";

    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private Context mContext;
    private HashMap<String, Object> mServiceMap;

    public HttpHelper(Context context) {

        mContext = context;
        mServiceMap = new HashMap<>();

        File file = new File(FileUtil.getCacheDirPath(mContext), "HttpCache");
        int size = 1024 * 1024 * 50;
        Cache cache = new Cache(file, size);

        try {
            SSLContext sslctxt = SSLContext.getInstance("TLS"); // 为请求通TLS协议，生成SSLContext对象
            sslctxt.init(null, new TrustManager[]{new FakeX509TrustManager()}, new java.security.SecureRandom());// 初始化SSLContext
            mOkHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .sslSocketFactory(sslctxt.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    })
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new CacheInterceptor())
                    .addNetworkInterceptor(new LogInterceptor())
                    .build();
        } catch (Exception e) {
        }
    }

    public HttpHelper(Context context, CommonParamsInterceptor commonParamsInterceptor) {

        mContext = context;
        mServiceMap = new HashMap<>();

        File file = new File(FileUtil.getCacheDirPath(mContext), "HttpCache");
        int size = 1024 * 1024 * 50;
        Cache cache = new Cache(file, size);

        try {
            SSLContext sslctxt = SSLContext.getInstance("TLS"); // 为请求通TLS协议，生成SSLContext对象
            sslctxt.init(null, new TrustManager[]{new FakeX509TrustManager()}, new java.security.SecureRandom());// 初始化SSLContext
            mOkHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .sslSocketFactory(sslctxt.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    })
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(commonParamsInterceptor)
                    .addInterceptor(new CacheInterceptor())
                    .addNetworkInterceptor(new LogInterceptor())
                    .build();
        } catch (Exception e) {
        }
    }


    /**
     * 创建ApiService
     *
     * @param clazz
     * @param baseUrl
     * @param <T>
     * @return
     */
    private <T> T createService(Class<T> clazz, String baseUrl) {
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return mRetrofit.create(clazz);
    }

    private <T> T createService(Class<T> clazz, String baseUrl,Gson gson) {
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return mRetrofit.create(clazz);
    }

    public <T> T getService(Class<T> clazz, String baseUrl) {
        if (mServiceMap.containsKey(baseUrl)) {
            return (T) mServiceMap.get(baseUrl);
        } else {
            Object obj = createService(clazz, baseUrl);
            mServiceMap.put(baseUrl, obj);
            return (T) obj;
        }
    }

    public <T> T getService(Class<T> clazz, String baseUrl, Gson gson) {
        if (mServiceMap.containsKey(baseUrl)) {
            return (T) mServiceMap.get(baseUrl);
        } else {
            Object obj = createService(clazz, baseUrl,gson);
            mServiceMap.put(baseUrl, obj);
            return (T) obj;
        }
    }

    public <T> T getService(Class<T> clazz, String baseUrl, String interfaceName) {
        return getService(clazz, baseUrl + interfaceName);
    }

    private class LogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            KLog.d(TAG, "HttpHelper" + String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();

            KLog.d(TAG, "HttpHelper" + String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            return response;

        }
    }

    private class CacheInterceptor implements Interceptor {

        // 有网络时 设置缓存超时时间1个小时
        int maxAge = 0;
        // 无网络时，设置超时为1个月
        int maxStale = 60 * 60 * 24 * 30;

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            if (!NetworkUtils.isNetworkAvailable(mContext)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response response = chain.proceed(request);
            if (NetworkUtils.isNetworkAvailable(mContext)) {
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }
}
