package com.baitu.fangyuan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baitu.fangyuan.utils.OtherUtils;

public class PopLoading {
    private Dialog popupWindow;
    private String text;
    private static PopLoading _instance;

    static public PopLoading getInstance() {
        if (_instance == null) {
            _instance = new PopLoading();
        }
        return _instance;
    }

    public PopLoading setText(String text) {
        this.text = text;
        return _instance;
    }

    public void setProgress(int progress){
        if (popupWindow != null) {
            String text = progress + "%";
            ((TextView) popupWindow.findViewById(R.id.textView_content)).setText(text);
        }
    }

    public void show(Context activity) {
        try {
            if (activity == null) return;
            if (popupWindow == null) {
                View root_view = LayoutInflater.from(activity).inflate(R.layout.customview_poploading, null);
                ImageView imageview = (ImageView) root_view.findViewById(R.id.imageView_title);
                imageview.setImageDrawable(activity.getResources().getDrawable(R.drawable.loading_anim));
                popupWindow = new Dialog(activity, R.style.Dialog_Not_Fullscreen);
                popupWindow.setContentView(root_view);
                popupWindow.getWindow().setWindowAnimations(R.style.AppBaseTheme);
                AnimationDrawable animationDrawable = (AnimationDrawable) imageview.getDrawable();
                animationDrawable.start();
            }
            if (text != null && !text.isEmpty()) {
                ((TextView) popupWindow.findViewById(R.id.textView_content)).setText(text);
            }
            if (activity instanceof Activity) {
                if (!((Activity) activity).isFinishing()) {
                    if (!popupWindow.isShowing())
                        popupWindow.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide(Context activity) {
        if (activity == null) return;
        if (activity instanceof Activity) {
            if (popupWindow != null) {
                Context context = OtherUtils.scanForActivity(popupWindow.getContext());
                if (activity == context) {
                    if (!((Activity) activity).isFinishing()) {
                        popupWindow.dismiss();
                    }
                    popupWindow = null;
                    _instance = null;
                }

            }
        }
    }
}
