package com.baitu.fangyuan.model;

import java.io.Serializable;

public class ADBean implements Serializable {

    /**
     * downloadUrl : http://static.phpjiayuan.com/chatapk/channel/fangyuan.apk
     * packageName : com.qingshu520.chat
     * sortNum : 1
     * adName : 美女直播
     * adIcon : http://huajian.h9a9.top/lmmy/data/star/5/4.jpg
     * enableCheck : 1
     * checked : 1
     */

    private String downloadUrl;
    private String packageName;
    private int sortNum;
    private String adName;
    private String adIcon;
    private int enableCheck;
    private int checked;

    public ADBean() {
    }

    public ADBean(String downloadUrl, String packageName, int sortNum, String adName, String adIcon, int enableCheck, int checked, boolean isInstall) {
        this.downloadUrl = downloadUrl;
        this.packageName = packageName;
        this.sortNum = sortNum;
        this.adName = adName;
        this.adIcon = adIcon;
        this.enableCheck = enableCheck;
        this.checked = checked;
        this.isInstall = isInstall;
    }

    public ADBean(String downloadUrl, String packageName, int sortNum, String adName, String adIcon, int enableCheck, int checked) {
        this.downloadUrl = downloadUrl;
        this.packageName = packageName;
        this.sortNum = sortNum;
        this.adName = adName;
        this.adIcon = adIcon;
        this.enableCheck = enableCheck;
        this.checked = checked;
    }

    /**
     * 是否已经安装
     */
    private boolean isInstall;

    public boolean isInstall() {
        return isInstall;
    }

    public void setInstall(boolean install) {
        isInstall = install;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getSortNum() {
        return sortNum;
    }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdIcon() {
        return adIcon;
    }

    public void setAdIcon(String adIcon) {
        this.adIcon = adIcon;
    }

    public int getEnableCheck() {
        return enableCheck;
    }

    public void setEnableCheck(int enableCheck) {
        this.enableCheck = enableCheck;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
