package com.baitu.fangyuan.OkHttpUtils.builder;


import com.baitu.fangyuan.OkHttpUtils.OkHttpUtils;
import com.baitu.fangyuan.OkHttpUtils.request.OtherRequest;
import com.baitu.fangyuan.OkHttpUtils.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
