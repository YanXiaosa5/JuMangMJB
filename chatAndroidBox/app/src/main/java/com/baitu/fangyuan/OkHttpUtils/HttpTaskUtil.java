package com.baitu.fangyuan.OkHttpUtils;


import com.baitu.fangyuan.OkHttpUtils.callback.Callback;
import com.baitu.fangyuan.data.SharedPreference;
import com.baitu.fangyuan.encry.aes.AesException;
import com.baitu.fangyuan.encry.aes.EncryAndroid;
import com.baitu.fangyuan.encry.sgin.SignUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.MediaType;

public class HttpTaskUtil {
    //final static String ANDROID_DEVICE_CODE = "2";
    //public final static String BASE_URL = "http://api.successcgb.com/";
    //api地址
    public final static String BASE_URL = "http://47.105.61.82";
//    public final static String BASE_URL = "http://10.0.0.180:8080";
    //public final static String BASE_URL_API = BASE_URL + "/api/";
    //public final static String BASE_URL_API = "http://192.168.19.101:8080/api/";
    private final static String JSON_TYPE = "application/json; charset=utf-8";
    private final static String TICKET = "ticket";
    private final static String ENCRYPTSTR = "encryptstr";
    private final static String TIMESTAMP = "timestamp";
    private final static String ECHOSTR = "echostr";
    private final static String SIGN = "sign";
    private final static String APP_KEY = "7908b2179af04e1099877643ad7c83a2";

    public static void doJsonTask(String url, Callback callback) {
        //doJsonTask(url, null, callback);
    }

    public static void doJsonTask(String url, Map<String, String> params, Callback callback) {
    }

    public static void doJsonTask(String url, String json, Callback callback) {
        //String json = getJsonString(params);
        try {
//            String encryptstr = CryptUtil.encrypt(json);
            String encryptstr = EncryAndroid.encrypt(json);

            String timestamp = System.currentTimeMillis() + "";
            String echostr = createRandom(false, 32);
            SortedMap<String, String> signMap = new TreeMap<>();
            signMap.put(ECHOSTR, echostr);
            signMap.put(ENCRYPTSTR, encryptstr);
            signMap.put(TIMESTAMP, timestamp);
            String sign = SignUtil.createSign(signMap, APP_KEY);
            signMap.put(SIGN, sign);

            OkHttpUtils.postString().url(BASE_URL + url).content(getJsonString(signMap))
                    .mediaType(MediaType.parse(JSON_TYPE))
                    .build().execute(callback);
//            OkHttpUtils.post().url(BASE_URL + url)
//                    .addParams(ENCRYPTSTR, json)
//                    .addParams(TIMESTAMP, SharedPreference.getTicket())
//                    .addParams(ECHOSTR, json)
//                    .addParams(SIGN, json)
//                    .build().execute(callback);

//            OkHttpUtils.post().url(BASE_URL + url)
//                    .addParams(ENCRYPTSTR, encryptstr)
//                    .addParams(TIMESTAMP, timestamp)
//                    .addParams(ECHOSTR, echostr)
//                    .addParams(SIGN, SignUtil.createSign(signMap, APP_KEY))
//                    .build().execute(callback);
        } catch (AesException e) {
            e.printStackTrace();
        }
    }

    private static String getJsonString(Map<String, String> params) {
        JSONObject jsonObject = new JSONObject();
        if (params == null) {
            params = new HashMap<>();
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(jsonObject);
    }

    public static void doFormTask(String objName, String url, String json, Callback callback) {
        doFormTask(objName, url, json, null, callback);
    }

    public static void doFormTask(String objName, String url, Map<String, String> params, Callback callback) {
        doFormTask(objName, url, null, params, callback);
    }

    public static void doFormTask(String objName, String url, String json, Map<String, String> params, Callback callback) {
        if (json == null) {
            JSONObject jsonObject = new JSONObject();
            if (params == null) {
                params = new HashMap<>();
            }

            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            json = String.valueOf(jsonObject);
        }

        OkHttpUtils.post().url(BASE_URL + url).addParams(objName, json)
                .addParams(TICKET, SharedPreference.getTicket()).build().execute(callback);
    }

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }
}
