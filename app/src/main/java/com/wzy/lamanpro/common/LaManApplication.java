package com.wzy.lamanpro.common;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.wzy.lamanpro.BuildConfig;

public class LaManApplication extends Application {


    private static LaManApplication appContext;
    public static boolean canUseUsb;
    public static boolean isManager;
    public static boolean easyMode;

    public static LaManApplication getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        easyMode = true;
        appContext = this;
        initOkgo();
        initStetho();
        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(new AndroidLogAdapter());


        }
    }

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }

    private void initOkgo() {
        OkGo.getInstance().init(this);
        OkGo.getInstance().init(this).setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE);
    }
}
