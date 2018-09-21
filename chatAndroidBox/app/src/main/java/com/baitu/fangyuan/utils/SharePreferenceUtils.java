package com.baitu.fangyuan.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {

    public static void save(Context context, int parentIndex, int childIndex){

        SharedPreferences index = context.getSharedPreferences("index", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = index.edit();
        edit.putInt("parentIndex",parentIndex);
        edit.putInt("childIndex",childIndex);
        edit.commit();

    }

    /**
     * type = 1   父索引
     * type = 2   子索引
     * @param type
     * @param context
     * @return
     */
    public static int getIndex(int type,Context context){
        SharedPreferences index = context.getSharedPreferences("index", Context.MODE_PRIVATE);
        int getIndex = index.getInt(type == 1 ? "parentIndex" : "childIndex", 0);
        return getIndex;
    }

}
