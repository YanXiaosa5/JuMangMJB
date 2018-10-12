package com.baitu.fangyuan.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baitu.fangyuan.R;

/**
 * Created by steven on 2016/12/1
 */
public class UIHelper {
//    private static LoadingDialog loadingDialog = null;

//    public static void showLoading(Context aContext){
//        loadingDialog = new LoadingDialog(aContext);
//        loadingDialog.show();
//    }
//
//    public static void hideLoading(){
//        if(loadingDialog!=null){
//            loadingDialog.dismiss();
//        }
//    }

    private static Toast mToast = null;

    public static void showToast(Context aContext, String msg, int duration) {
        if (mToast == null) {
            View view = LayoutInflater.from(aContext).inflate(R.layout.toast, null);
            view.getBackground().setAlpha(180);
            mToast = new Toast(aContext);
            mToast.setView(view);
            TextView txtvMessage = (TextView) view.findViewById(R.id.txtvMessage);
            txtvMessage.setText(msg);
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.BOTTOM, 0, 20);
        } else {
            View view = mToast.getView();
            TextView txtvMessage = (TextView) view.findViewById(R.id.txtvMessage);
            txtvMessage.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void showShortToast(Context aContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(aContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showLongToast(Context aContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(aContext, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    public static void showToastOnUi(final Context aContext, final String msg, final int duration) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    View view = LayoutInflater.from(aContext).inflate(R.layout.toast, null);
                    view.getBackground().setAlpha(180);
                    mToast = new Toast(aContext);
                    mToast.setView(view);
                    TextView txtvMessage = (TextView) view.findViewById(R.id.txtvMessage);
                    txtvMessage.setText(msg);
                    mToast.setDuration(duration);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                } else {
                    View view = mToast.getView();
                    TextView txtvMessage = (TextView) view.findViewById(R.id.txtvMessage);
                    txtvMessage.setText(msg);
                    mToast.setDuration(duration);
                }
                mToast.show();
            }
        });
    }

    public static void showToast(Context aContext, int msgId, int duration) {
        if (mToast == null) {
            View view = LayoutInflater.from(aContext).inflate(R.layout.toast, null);
            view.getBackground().setAlpha(180);
            mToast = new Toast(aContext);
            mToast.setView(view);
            TextView txtvMessage = (TextView) view.findViewById(R.id.txtvMessage);
            txtvMessage.setText(aContext.getResources().getString(msgId));
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
        } else {
            View view = mToast.getView();
            TextView txtvMessage = (TextView) view.findViewById(R.id.txtvMessage);
            txtvMessage.setText(aContext.getResources().getString(msgId));
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void post(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static void postDelayed(Runnable runnable, long time) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, time);
    }
}
