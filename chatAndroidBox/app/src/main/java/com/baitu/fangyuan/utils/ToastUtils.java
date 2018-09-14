package com.baitu.fangyuan.utils;

import android.widget.Toast;

import com.baitu.fangyuan.MyApplication;

public class ToastUtils {
    static ToastUtils instance;
    private Toast mToast;

    public static ToastUtils getInstance() {
        if (instance == null) {
            instance = new ToastUtils();
        }
        return instance;
    }

    public Toast showToast(String tips, int length) {
        cancelToast();
        mToast = Toast.makeText(MyApplication.applicationContext, tips, length == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        return mToast;
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
