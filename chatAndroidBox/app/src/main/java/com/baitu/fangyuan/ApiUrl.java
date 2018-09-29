package com.baitu.fangyuan;

import com.baitu.fangyuan.OkHttpUtils.HttpTaskUtil;
import com.baitu.fangyuan.OkHttpUtils.callback.Callback;
import com.baitu.fangyuan.utils.StringUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口类
 */
public class ApiUrl {

    /**
     * 广告接口
     */
    public static final String APPLICATION_LIST ="/api/ad/list/v1";

    /**
     * appkey
     */
    public static final String APP_KEY = "121bcdaadcc74a959c5402a1825ef74f";

    /**
     * 获取广告列表
     *
     * @param callback  处理数据的回调
     */
    public static void adList(Callback callback){
        Map<String, String> params = new HashMap<>();
        params.put("appKey", APP_KEY);
        params.put("channelId", MyApplication.CHANNEL_NAME);
        StringUtils.saveFile(MyApplication.CHANNEL_NAME,"channelId");
        HttpTaskUtil.doJsonTask(APPLICATION_LIST, new Gson().toJson(params), callback);
    }

}
