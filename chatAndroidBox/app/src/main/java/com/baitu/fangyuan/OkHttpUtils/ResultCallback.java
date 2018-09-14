package com.baitu.fangyuan.OkHttpUtils;

import android.app.ProgressDialog;
import android.content.Context;

import com.baitu.fangyuan.OkHttpUtils.callback.Callback;
import com.baitu.fangyuan.encry.aes.AesException;
import com.baitu.fangyuan.encry.aes.EncryAndroid;
import com.baitu.fangyuan.model.ResponseBean;
import com.baitu.fangyuan.utils.JSONUtils;
import com.baitu.fangyuan.utils.UIHelper;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by steven on 2018/1/5.
 * Email-songzhonghua_1987@msn.com
 */

public abstract class ResultCallback<T> extends Callback<T> {
    private Type mType;
    private boolean showLoading = false;
    private boolean customJson = false;

    /**
     * Loading Dialog
     **/
    private ProgressDialog mProgressDialog;
    private String showText = "Load...";
    public Context mContext;

    public ResultCallback(Context aContext) {
        this(aContext, false);
    }

    public ResultCallback(Context aContext, boolean customJson) {
        this(aContext, false, "", customJson);
    }

    public ResultCallback(Context aContext, boolean showLoading, boolean customJson) {
        this(aContext, showLoading, "", customJson);
    }

    public ResultCallback(Context aContext, boolean showLoading, String showText, boolean customJson) {
        mType = getSuperclassTypeParameter(getClass());
        this.mContext = aContext;
        this.showLoading = showLoading;
        this.showText = showText;
        this.customJson = customJson;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        onFailure(e.getMessage());
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if (showLoading) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(mContext);
            } else {
                mProgressDialog.cancel();
            }
            mProgressDialog.setMessage(showText);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    @Override
    public void inProgress(int progress, long total, int id) {
        super.inProgress(progress, total, id);
        onLoading(progress, total);
    }

    public void onLoading(float progress, long total) {
    }

    /**
     * 关闭ProgressDialog
     **/
    private void initCloseProgressDialog() {
        if (showLoading && mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            return null;
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        final String string = response.body().string();
        OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
            @Override
            public void run() {
                onResponseString(string);
            }
        });
        if (string != null && string.length() > 0) {
            try {
                if (customJson) {
                    return null;
                } else {
                    JSONObject obj = new JSONObject(string);
                    String data = "";
                    if (obj.has("data")) {
                        data = obj.getString("data");
                    }
                    final int status = obj.getInt("code");
                    final String message = obj.getString("message");
                    final String finalData = getResult(data);
//                    OkHttpUtils.getInstance().getDelivery().execute(() -> onSuccessString(finalData, message, status));

                    OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                        @Override
                        public void run() {
                            onSuccessString(finalData, message, status);
                        }
                    });
                    switch (status) {
                        case 200:
                            if (mType != null) {
                                return new Gson().fromJson(finalData, mType);
                            } else {
                                return null;
                            }
                        case 201:
                            onNoLogin();
                            return null;
                        default:
                            onSpecialStatus(status, message);
                            throw new Exception(message);
                    }
                }
            } catch (JSONException e) {
                throw new Exception("unknown json type");
            }
        } else {
            throw new Exception("none data");
        }
    }

    private String getResult(String data) {
        String result = "";
        if (data != null && data.length() > 0 && !data.equals("null")) {
            ResponseBean responseBean = JSONUtils.fromJson(data, ResponseBean.class);
            try {
//                result = CryptUtil.decrypt(responseBean.getEncryptstr());
                result = EncryAndroid.decrypt(responseBean.getEncryptstr());
            } catch (AesException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void onResponse(T response, int id) {
        onSuccess();
        onSuccess(response);
    }

    public void onSuccess(T response) {
        initCloseProgressDialog();
    }

    public void onSuccessString(String data, String message, int code) {
        initCloseProgressDialog();
    }

    public void onSuccess() {
        initCloseProgressDialog();
    }

    public void onResponseString(String response) {
        initCloseProgressDialog();
    }

    public void onSpecialStatus(int status, String message) {//特殊状态码处理

    }

    public void onNoLogin() {
//        OkHttpUtils.getInstance().getDelivery().execute(() -> {
//            initCloseProgressDialog();
//            doLoginOut();
//        });
        OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
            @Override
            public void run() {
                initCloseProgressDialog();
                doLoginOut();
            }
        });
    }

    public void doLoginOut() {

    }

    public void onResultError(String message) {
        initCloseProgressDialog();
        if (message != null && message.length() > 0) {
            UIHelper.showLongToast(mContext, message);
        } else {
            UIHelper.showLongToast(mContext, "Server Error");
        }
    }

    public void onFailure(String strMsg) {
        if (strMsg != null && strMsg.length() > 0) {
            UIHelper.showLongToast(mContext, strMsg);
        } else {
            UIHelper.showLongToast(mContext, "Net Error");
        }
        initCloseProgressDialog();
    }
}

