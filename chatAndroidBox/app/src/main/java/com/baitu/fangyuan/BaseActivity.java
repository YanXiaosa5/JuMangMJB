package com.baitu.fangyuan;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.baitu.fangyuan.utils.ScreenUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // 透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        initView();
        setStatusBarStyle(R.color.white, false, true);
    }

    private LayoutInflater inflater;
    private LinearLayout mainView;
    private LinearLayout.LayoutParams mmlayoutparams;
    private View statusBar;

    private void initView() {
        inflater = LayoutInflater.from(this);
        mainView = new CustomInsetsLinearLayout(this);
        mainView.setOrientation(LinearLayout.VERTICAL);
        mmlayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        initStatusBar();
        mainView.setFitsSystemWindows(true);
        setContentView(mainView, mmlayoutparams);
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            statusBar = new View(this);
            statusBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.getStatusBarHeight(this)));
            mainView.addView(statusBar);
        }
    }

    public void setStatusBarColor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)
        }
        if (statusBar != null) {
            statusBar.setBackgroundColor(getResources().getColor(id));
        }
    }

    /**
     * 设置状态栏样式（兼容miui和flyme系统和系统6.0以上系统）
     * @param color 颜色
     * @param transparent_bg 状态栏是否透明
     * @param font_dark_color 状态栏是否深色字体
     */
    public void setStatusBarStyle(int color, boolean transparent_bg , boolean font_dark_color){
        if (Build.VERSION.SDK_INT >= 23) {
            Window win = getWindow();
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            setStatusBarColor(color);
            if(font_dark_color){
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }else{
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            if(transparent_bg){
                win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            Class clazz = win.getClass();

            // miui
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(win, font_dark_color?darkModeFlag:0, darkModeFlag);
            } catch (Exception e) {
            }

            // flyme
            try {
                WindowManager.LayoutParams lp = win.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if(font_dark_color){
                    value |= bit;
                }else{
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                win.setAttributes(lp);
            } catch (Exception e) {
            }
        }
    }

    public void hideStatusBar() {
        if (statusBar != null)
            statusBar.setVisibility(View.GONE);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (inflater == null) {
            initView();
        }
        mainView.addView(inflater.inflate(layoutResID, null), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //被系统回收,关闭所有弹出的dialog
        PopLoading.getInstance().hide(this);
    }

    @Override
    public void finish() {
        PopLoading.getInstance().hide(this);
        super.finish();
    }
}
