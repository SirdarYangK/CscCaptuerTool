package com.csc.capturetool.myapplication.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by SirdarYangK on 2018/11/2
 * des: 线程池工具类
 */
public class ExecutorInstance {
    
    /** 线程池执行器 */
    private static ExecutorService executor = null;
    
    /** 线程池并发线程数,服务器请求减少并发数，一个一个请求 */
    private static final int POOL_SIZE = 10;
    
    /**
     * 获取线程池执行器
     */
    public static ExecutorService getExecutor(){
        if(executor == null){
            executor = Executors.newFixedThreadPool(POOL_SIZE, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(Thread.NORM_PRIORITY - 1);
                    return t;
                }
            });
        }
        return executor;
    }
    
}
