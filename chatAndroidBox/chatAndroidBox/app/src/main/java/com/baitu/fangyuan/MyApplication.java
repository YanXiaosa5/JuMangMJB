package com.baitu.fangyuan;

import android.app.Application;
import android.content.Context;
import com.baitu.fangyuan.BuildConfig;
import com.baitu.fangyuan.log.Log;
import com.baitu.fangyuan.utils.OtherUtils;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    public static Context applicationContext;
    public static int VERSION_CODE;
    public static String VERSION_NAME;

    /**
     * 渠道号
     */
    public static String CHANNEL_NAME;
    private static MyApplication myApplication;

    public synchronized static MyApplication getInstance() {
        return myApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
        myApplication = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        Log.setDebug(BuildConfig.DEBUG);//common日志开关，必须放在最前面
        Log.e(TAG, "初始化 Application begin");
        VERSION_CODE = OtherUtils.getVersionCode(this);
        VERSION_NAME = OtherUtils.getVersionName(this);
        String temp_channel_name = OtherUtils.readChannel(this);//渠道原始字符串   友盟渠道号:灯塔渠道号
        String[] split = temp_channel_name.split(":");//   yyb1:1   友盟渠道号:灯塔渠道号
        if (split.length > 1) {
            CHANNEL_NAME = split[0];
        } else {
            CHANNEL_NAME = temp_channel_name;
        }
        Log.e(TAG, "初始化 Application end");
    }
}