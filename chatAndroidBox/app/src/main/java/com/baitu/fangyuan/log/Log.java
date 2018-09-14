/*
 * @Author: HePing 1272287952@qq.com
 * @Copyright (c) 2017. Shanghai white rabbit Network Technology Co., Ltd. All rights reserved.
 */

package com.baitu.fangyuan.log;


import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static boolean debug = false;

    public static void setDebug(boolean d)
    {
      debug  = d;
    }

    public static void i(String tag, String msg)
    {
        if (debug)
        {
            android.util.Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg)
    {
        if (debug)
        {
            android.util.Log.v(tag, msg);
        }
    }


    public static void d(String tag, String msg)
    {
        if (debug)
        {
            android.util.Log.d(tag, msg);
        }
    }


    public static void e(String tag, String msg)
    {
        if (debug)
        {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable throwable)
    {
        if (debug)
        {
            android.util.Log.e(tag, msg, throwable);
        }
    }

    public static void w(String tag, String msg)
    {
        if (debug)
        {
            android.util.Log.w(tag, msg );
        }
    }

    public static void w(String tag, String msg, Throwable throwable)
    {
        if (debug)
        {
            android.util.Log.w(tag, msg, throwable);
        }
    }

    private static Boolean MYLOG_WRITE_TO_FILE=false;// 日志写入文件开关
    public static int Tag = 0;
    private static String MYLOGFILEName = " Log.txt";// 本类输出的日志文件名称
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat(
            "HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd HH");// 日志文件格式

    /**
     * 打开日志文件并写入日志
     *
     * @return
     * **/
    public static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
        if (debug){
            Date nowtime = new Date();
            String needWriteFiel = logfile.format(nowtime);
            String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype
                    + "    " + tag + "    " + text;
            String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
            StringBuffer buffer = new StringBuffer();
            buffer.append(sdCard).append(File.separator).append("chat").append(File.separator).append("log").append(File.separator);
            try {
                File file1 = new File(buffer.toString());
                if (!file1.exists())
                {
                    file1.mkdirs();
                }
                File file = new File(buffer.toString(), needWriteFiel
                    + "h " + Tag + MYLOGFILEName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(needWriteMessage);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
