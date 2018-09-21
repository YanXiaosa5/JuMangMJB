package com.baitu.fangyuan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baitu.fangyuan.OkHttpUtils.OkHttpUtils;
import com.baitu.fangyuan.OkHttpUtils.ResultCallback;
import com.baitu.fangyuan.OkHttpUtils.callback.FileCallBack;
import com.baitu.fangyuan.log.Log;
import com.baitu.fangyuan.model.ADBean;
import com.baitu.fangyuan.utils.GsonUtils;
import com.baitu.fangyuan.utils.SharePreferenceUtils;
import com.baitu.fangyuan.utils.StringUtils;
import com.baitu.fangyuan.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.DownloadListenerBunch;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;

public class MainActivity extends BaseActivity {

    private static final String TAG = "System.out";
    private Dialog dialog;
    private boolean isDownloaded = false;
    private boolean isDownloading = false;
    private boolean isClickOpen = false;
    private File file;
    private boolean onCreate = true;

    /**
     * 广告信息类
     */
    private ADBean mAdBean;

    /**
     * 接收广告列表
     */
    private List<ADBean> mAdBeans;

    /**
     * 将包名和对应的广告类分组
     */
    private LinkedHashMap<String,List<ADBean>> dataMap = new LinkedHashMap();

    /**
     * 父数据索引
     */
    private int parentIndex = 0;

    /**
     * 子数据索引
     */
    private int childIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (onCreate) {
            onCreate = false;
        } else {
            finish();
            return;
        }
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            finish();
            return;
        } else {
            hideStatusBar();
            setContentView(R.layout.splash_activity);
        }
        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }

        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        wl.alpha = 0.6f;//这句就是设置窗口里控件的透明度的．０.０全透明．１.０不透明．
        window.setAttributes(wl);

        findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getShareIndex();
        requestAD();
    }

    /**
     * 弹出推荐框
     */
    private void showRecommendDialog() {
        dialog = new Dialog(this, R.style.DialogErr);
        dialog.setContentView(R.layout.dialog_recommend);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        //复选框
        final CheckBox cb_can_choice = (CheckBox) dialog.findViewById(R.id.cb_can_choice);
        Drawable drawable = getResources().getDrawable(R.drawable.cb_select);
        drawable.setBounds(0, 0, 54, 54);
        cb_can_choice.setCompoundDrawables(drawable, null, null, null);

        //app名称
        TextView tv_app_name = (TextView) dialog.findViewById(R.id.tv_app_name);

        //图标
        ImageView iv_icon = (ImageView) dialog.findViewById(R.id.iv_icon);

        //确定按钮
        final Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

        if (mAdBean != null) {
            if (mAdBean.getAdIcon() != null && !isFinishing()) {
                Glide.with(this).load(mAdBean.getAdIcon()).into(iv_icon);
            } else {
                Random random = new Random();
                int i = random.nextInt(6);
                iv_icon.setImageResource(icons[i]);
            }
            //设置app名称
            tv_app_name.setText(mAdBean.getAdName() == null ? "美女直播" : mAdBean.getAdName());
            //是否启用可勾选功能 1启用 2禁用
            int enableCheck = mAdBean.getEnableCheck();
            //是否选中 1选中 0未选中
            int checked = mAdBean.getChecked();
            cb_can_choice.setChecked(checked == 1 ? true : false);
            cb_can_choice.setClickable(enableCheck == 1 ? true : false);
        }

        dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                if(!cb_can_choice.isChecked()){
                    sureClose();
                }
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cb_can_choice.isChecked()){
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    sureCancel();
                    finish();
                    return;
                }

                isClickOpen = true;
                if (!isDownloaded) {
                    PopLoading.getInstance().setText("加载中...").show(MainActivity.this);
                    if (mAdBean != null) {
                        openOtherApp(mAdBean);
                    }
                } else {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
//                    finish();
                    System.out.println("没有下载过finish");
                    openFile(MainActivity.this, file);
                }
            }
        });

        if(!dialog.isShowing())
        dialog.show();
    }

    /**
     * 直接点击关闭
     */
    public void cancel(){
        MobclickAgent.onEvent(getApplicationContext(),"cancel",MyApplication.CHANNEL_NAME);
    }

    /**
     * 未选中复选框,点击确定
     */
    public void sureCancel(){
        MobclickAgent.onEvent(getApplicationContext(),"sureCancel",MyApplication.CHANNEL_NAME);
    }

    /**
     * 未选中复选框,点击关闭
     */
    public void sureClose(){
        MobclickAgent.onEvent(getApplicationContext(),"sureClose",MyApplication.CHANNEL_NAME);
    }


    private int[] icons = {R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5, R.drawable.icon6};

    /**
     * 下载apk
     */
    private void openOtherApp(ADBean resourcesBean) {
        //新判断
        if (!isDownloading) {
            startDownLoad(resourcesBean);
        }

    }

    //yxs
    @Override
    protected void onRestart() {
        super.onRestart();
        getShareIndex();
        List<String> keyStrings = filterKey(mAdBeans);
        choiceOne(keyStrings);
    }

    /**
     * 获取上一次缓存的数据
     */
    public void getShareIndex(){
        //获取上一次缓存的索引
        parentIndex = SharePreferenceUtils.getIndex(1,this);
        childIndex = SharePreferenceUtils.getIndex(2,this);
        System.out.println("获取数据"+parentIndex+"<=====>"+childIndex);
    }

    /**
     * 下载
     *  yxs
     * @param adBean
     */
    public void startDownLoad(ADBean adBean) {
        MobclickAgent.onEvent(getApplicationContext(),"startDownLoad",MyApplication.CHANNEL_NAME);
        String url = adBean.getDownloadUrl() == null ? "https://cdn.138pool.com/apk/huajian_AZRONG_su3.apk" : adBean.getDownloadUrl();
        isDownloading = true;
        PopLoading.getInstance().show(this);
        String[] split = url.split("/");
        String path = getCacheDir().getPath();
        String apkName = split == null ? "fanhua.apk" : split[split.length - 1];

        file = new File(path + File.separator + apkName);
        if (file.exists()) {
            System.out.println("文件路径" + file.getAbsolutePath());
            isDownloading = false;
            openFile(this, file);
        } else {
            OkHttpUtils.get().url(url).build().execute(new FileCallBack(path, apkName) {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    isDownloading = false;
                }

                @Override
                public void onResponse(File response, int id) {
                    isDownloaded = true;
                    isDownloading = false;
                    if (isClickOpen) {
                        if (dialog != null && dialog.isShowing() && !isFinishing()) {
                            dialog.dismiss();
                        }
//                        finish();
                        System.out.println("下载完成后跳转安装finish");
                        openFile(MainActivity.this, response);
                    }
                }

                @Override
                public void inProgress(int progress, long total, int id) {
                    super.inProgress(progress, total, id);
                    PopLoading.getInstance().setProgress(progress);
                }
            });
        }
    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param file
     */
    public void openFile(Context context, File file) {

        MobclickAgent.onEvent(getApplicationContext(),"openFile",MyApplication.CHANNEL_NAME);

        try {
            String chmodCmd = "chmod 666 " + file.getAbsolutePath();
            Runtime.getRuntime().exec(chmodCmd);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
//            context.startActivity(intent);
            startActivityForResult(intent,10001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10001){

            //从安装页面回来，判断是否安装了,安装了childIndex要增加1,此判断要放在getShareIndex后面,否则会覆盖
            getShareIndex();

            if(StringUtils.isAvilible(getApplicationContext(),mAdBean.getPackageName())){
                childIndex++;
            }
            System.out.println("新的childIndex"+childIndex);
            List<String> keyStrings = filterKey(mAdBeans);
            choiceOne(keyStrings);
        }
    }

    /**
     * 请求广告接口
     */
    public void requestAD() {
        MobclickAgent.onEvent(getApplicationContext(),"requestAD",MyApplication.CHANNEL_NAME);
        ApiUrl.adList(new ResultCallback<List<ADBean>>(this) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onSuccess(List<ADBean> response) {
                super.onSuccess(response);
                System.out.println("获取广告数据:"+ GsonUtils.toJson(response));
                mAdBeans = response;
                List<String> keyStrings = filterKey(mAdBeans);
                if(keyStrings != null && keyStrings.size() > 0) {
                    choiceOne(keyStrings);
                }else{
                    defaultADbean();
                }

            }
        });
    }

    /**
     * 无数据默认
     */
    public void defaultADbean(){
        String downloadUrl = "https://cdn.138pool.com/apk/huajian_AZRONG_su3.apk";
        String adIcon = "http://huajian.h9a9.top/lmmy/data/star/5/4.jpg";
        ADBean adBean = new ADBean();
        adBean.setAdIcon(adIcon);
        adBean.setAdName("美女直播");
        adBean.setChecked(1);
        adBean.setEnableCheck(0);
        adBean.setDownloadUrl(downloadUrl);
        mAdBean = adBean;
        showRecommendDialog();
    }

    /**
     * 从元数据中取出所有的key值,进行分类,以便于map添加分组数据(没有关联索引)
     * @param adBeans
     */

    public List<String> filterKey(List<ADBean> adBeans){

        final List<String> keyList = new ArrayList<>();
        for (int i = 0; i < adBeans.size(); i++) {
            ADBean adBean = adBeans.get(i);
            if(!keyList.contains(adBean.getPackageName())){
                keyList.add(adBean.getPackageName());
            }
        }

        if(keyList.size() <= 0){
            return keyList;
        }
        //双层循环,将key和对应的adbean分组,一个key(包名)对应多个渠道的app(adbean)
        for (int i = 0; i < keyList.size(); i++) {//key数据

            String key = keyList.get(i);//key值
            List<ADBean> categoryList = new ArrayList<>();

            for (int j = 0; j < adBeans.size(); j++) {//元数据
                System.out.println("key"+key);
                ADBean adBean = adBeans.get(j);//每一个广告信息
                String packageName = adBean.getPackageName();

                if(packageName.equals(key)){
                    categoryList.add(adBean);
                }
            }
            dataMap.put(key,categoryList);
        }

        //打印map中所有信息
        System.out.println("分类长度:"+dataMap.size());
        for (Map.Entry<String, List<ADBean>> entry : dataMap.entrySet()) {
            System.out.println("\n包名key:" + entry.getKey()+"对应:-->"+new Gson().toJson(entry.getValue()));
        }

        return keyList;
    }

    /**
     * 顺序选择(关联索引)
     * @param keyList
     */
    public void choiceOne(List<String> keyList){

        if(parentIndex > keyList.size() - 1){
            parentIndex = 0;
            childIndex = 0;
        }

        List<ADBean> adBeans = dataMap.get(keyList.get(parentIndex));

        if(adBeans != null && adBeans.size() > 0){
            String packageName = adBeans.get(0).getPackageName();
            if(!StringUtils.isAvilible(getApplicationContext(),packageName)){
                if(childIndex >= adBeans.size()){
                    parentIndex++;
                    if(parentIndex >= dataMap.size()) {
                        parentIndex = 0;
                    }
                    childIndex = 0;
                }
            }else{//如果已经安装
                for (int i = parentIndex; i < keyList.size(); i++) {
                    if(!StringUtils.isAvilible(getApplicationContext(),keyList.get(i))){
                        parentIndex = i;
                        break;
                    }
                }

                if(parentIndex > dataMap.size() - 1) {
                    parentIndex = 0;
                }
                childIndex = 0;
            }
            mAdBean = dataMap.get(keyList.get(parentIndex)).get(childIndex);
            System.out.println("parent="+parentIndex+"<------->"+"childIndex="+childIndex+"===>sort"+GsonUtils.toJson(dataMap.get(keyList.get(parentIndex)).get(childIndex)));
            //在这里就要缓存索引，避免用户杀死进程，无法缓存
            shareIndex();
            showRecommendDialog();
        }else{
            defaultADbean();
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        shareIndex();
    }

    /**
     * 缓存索引
     */
    public void shareIndex(){
        SharePreferenceUtils.save(this,parentIndex,childIndex);
        System.out.println("缓存的数据:"+parentIndex+"<--->"+childIndex);
    }

    @Override
    public void finish() {
        childIndex++;
        shareIndex();
        super.finish();
    }
}