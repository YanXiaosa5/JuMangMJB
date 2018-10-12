package com.baitu.fangyuan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.baitu.fangyuan.BuildConfig;
import com.meituan.android.walle.ChannelInfo;
import com.meituan.android.walle.WalleChannelReader;

import java.security.MessageDigest;
import java.util.Map;

public class OtherUtils {

    public static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());
        return null;
    }

    /**
     * md5加密
     *
     * @param s
     * @return
     */
    public static String md5_code(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = s.getBytes("UTF-8");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = (md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取软件版本信息
     */
    public static int getVersionCode(Context ctx) {
        int version_code = 0;
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pinfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            version_code = pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version_code;
    }

    /**
     * 获取软件版本信息
     */
    public static String getVersionName(Context ctx) {
        String version_name = "";
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pinfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            version_name = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version_name;
    }

    public static String readChannel(Context ctx) {
        final ChannelInfo channelInfo = WalleChannelReader.getChannelInfo(ctx);

        if (channelInfo != null) {
            return channelInfo.getChannel();
        }
        if (BuildConfig.DEBUG) {
            return "dev";
        } else {
            return "default";//改为default，因为有的三方渠道，会更改channelInfo
        }
    }
}