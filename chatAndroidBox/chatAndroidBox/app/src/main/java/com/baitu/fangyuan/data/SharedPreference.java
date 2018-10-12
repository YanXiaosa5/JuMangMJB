package com.baitu.fangyuan.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.baitu.fangyuan.MyApplication;

/**
 * Created by steven on 2017/10/23.
 * Email-songzhonghua_1987@msn.com
 */

public class SharedPreference {
    private final static SharedPreferences sSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());

    private static void setPreference(String PrefId, String value) {
        if (TextUtils.isEmpty(value)) {
            sSharedPreferences.edit().remove(PrefId).apply();
        } else {
            sSharedPreferences.edit().putString(PrefId, value).apply();
        }
    }

    public static void setPreference(String PrefId, boolean value) {
        sSharedPreferences.edit().putBoolean(PrefId, value).apply();
    }

    public static void setPreference(String PrefId, int value) {
        sSharedPreferences.edit().putInt(PrefId, value).apply();
    }

    public static void setPreference(String PrefId, long value) {
        sSharedPreferences.edit().putLong(PrefId, value).apply();
    }

    public static void setStringPreference(String PrefId, String value) {
        sSharedPreferences.edit().putString(PrefId, value).apply();
    }

    private static String getPreference(String PrefId) {
        return getPreference(PrefId, "");
    }

    public static String getPreference(String PrefId, String defaultValue) {
        return sSharedPreferences.getString(PrefId, defaultValue);
    }

    public static int getPreference(String PrefId, int defaultValue) {
        return sSharedPreferences.getInt(PrefId, defaultValue);
    }

    public static long getPreference(String PrefId, long defaultValue) {
        return sSharedPreferences.getLong(PrefId, defaultValue);
    }

    public static boolean getBooleanPreference(String PrefId, boolean defaultValue) {
        return sSharedPreferences.getBoolean(PrefId, defaultValue);
    }

//    private static final String SESSION_ID = "sessionId";
//
//    public static void setSessionId(String sessionId) {
//        setPreference(SESSION_ID, sessionId);
//    }
//
//    public static String getSessionId() {
//        return getPreference(SESSION_ID);
//    }

    private static final String USER_ID = "USER_ID";

    public static void setUserId(int userId) {
        setPreference(USER_ID, userId);
    }

    public static int getUserId() {
        return getPreference(USER_ID, 0);
    }

    private static final String TICKET = "Ticket";

    public static void setTicket(String Ticket) {
        setPreference(TICKET, Ticket);
    }

    public static String getTicket() {
        return getPreference(TICKET);
    }

    private static final String USER_INFO = "USER_INFO";

    public static void setUserInfo(String userInfo) {
        setPreference(USER_INFO, userInfo);
    }

    public static String getUserInfo() {
        return getPreference(USER_INFO);
    }

    private static final String CITY = "city";

    public static void setCity(String city) {
        setPreference(CITY, city);
    }

    public static String getCity() {
        return getPreference(CITY, "苏州");
    }

}
