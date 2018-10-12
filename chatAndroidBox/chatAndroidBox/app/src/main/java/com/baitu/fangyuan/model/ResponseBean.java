package com.baitu.fangyuan.model;

import java.io.Serializable;

public class ResponseBean implements Serializable {
    private String echostr;
    private String encryptstr;
    private String sign;
    private String timestamp;

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
