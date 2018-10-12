package com.baitu.fangyuan.model;

import java.io.Serializable;

/**
 * 请求的接口数据格式
 */
public class RequestData implements Serializable {

    /**
     * 随机字符串32位
     */
    private String echostr;

    /**
     * aes加密后的json参数
     */
    private String encryptstr;

    /**
     * 加密签名
     */
    private String sign;

    /**
     * 当前时间戳
     */
    private String timestamp;

    public RequestData(String echostr, String encryptstr, String sign, String timestamp) {
        this.echostr = echostr;
        this.encryptstr = encryptstr;
        this.sign = sign;
        this.timestamp = timestamp;
    }

    public RequestData(String echostr, String encryptstr, String timestamp) {
        this.echostr = echostr;
        this.encryptstr = encryptstr;
        this.timestamp = timestamp;
    }

    public String getEchostr() {
        return echostr;
    }

    public void setEchostr(String echostr) {
        this.echostr = echostr;
    }

    public String getEncryptstr() {
        return encryptstr;
    }

    public void setEncryptstr(String encryptstr) {
        this.encryptstr = encryptstr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
