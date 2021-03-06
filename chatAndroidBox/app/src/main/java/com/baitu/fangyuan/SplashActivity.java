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
import com.baitu.fangyuan.utils.StringUtils;
import com.baitu.fangyuan.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.DownloadListenerBunch;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

public class SplashActivity extends BaseActivity {

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

        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
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
                    PopLoading.getInstance().setText("加载中...").show(SplashActivity.this);
                    if (mAdBean != null) {
                        openOtherApp(mAdBean);
                    }
                } else {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    finish();
                    openFile(SplashActivity.this, file);
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
//        String channel_apk = resourcesBean.getDownloadUrl();
//        if (!isDownloading) {
//            startDownload(SplashActivity.this, channel_apk);
//        }

        //新判断
        if (!isDownloading) {
            startDownLoad(resourcesBean);
        }

    }

    //yxs
    @Override
    protected void onRestart() {
        super.onRestart();
        installChoice(mAdBeans);

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
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
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
                        finish();
                        openFile(SplashActivity.this, response);
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
     * 开始下载
     *
     * @param activity
     * @param url
     */
    private void startDownload(final Activity activity, final String url) {
        isDownloading = true;
        String[] split = url.split("/");
        file = new File(getCacheDir(), split == null ? "fanhua.apk" : split[split.length - 1]);
        System.out.println(getCacheDir() + "缓存路径");
        Log.w(TAG, "startDownload: 开始下载" + file.getAbsolutePath());
        DownloadListener combinedListener = new DownloadListenerBunch.Builder()
                .append(new DownloadListener1() {
                    @Override
                    public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
                        Log.w(TAG, "startDownload: taskStart");
                        PopLoading.getInstance().show(SplashActivity.this);
                    }

                    @Override
                    public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
                        Log.w(TAG, "startDownload: retry");
                        isDownloaded = false;
                        PopLoading.getInstance().hide(SplashActivity.this);
                        ToastUtils.getInstance().showToast("重新下载", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
                        Log.w(TAG, "startDownload: connected");
                    }

                    @Override
                    public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                        int progress = (int) (1.0f * currentOffset / totalLength * 100);
                        PopLoading.getInstance().setProgress(progress);
                    }

                    @Override
                    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
                        PopLoading.getInstance().hide(SplashActivity.this);
                        if (cause == EndCause.COMPLETED) {
                            Log.w(TAG, "startDownload: taskEnd: COMPLETED: cause: " + cause);
                            isDownloaded = true;
                            if (isClickOpen) {
                                if (dialog != null && dialog.isShowing() && !activity.isFinishing()) {
                                    dialog.dismiss();
                                }
                                finish();
                                openFile(activity, SplashActivity.this.file);
                            }
                        } else {
                            isDownloading = false;
                            Log.w(TAG, "startDownload: taskEnd: 其他: cause: " + cause + " realCause : " + realCause);
                            StringUtils.saveFile("startDownload: taskEnd: 其他: cause: " + cause + " realCause : " + realCause, "download_error");
                            ToastUtils.getInstance().showToast("网络异常,下载失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();
        DownloadTask.Builder builder = new DownloadTask.Builder(url, this.file);
        builder.setMinIntervalMillisCallbackProcess(1000);
        builder.setPriority(10);
        builder.setReadBufferSize(8192);
        builder.setFlushBufferSize(32768);
        builder.setConnectionCount(5);
        DownloadTask task = builder.build();
        task.enqueue(combinedListener);
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
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
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
                installChoice(response);
            }
        });
    }

    /**
     * 选择安装哪一个安装包
     */
    public void installChoice(List<ADBean> response) {
        if (response != null && response.size() > 0) {

            //如果是顺序判断
            for (int i = 0; i < response.size(); i++) {
                ADBean resource = response.get(i);
                //如果已经安装
                if (StringUtils.isAvilible(getApplicationContext(), resource.getPackageName())) {
                    resource.setInstall(true);
                }
                //如果没有安装
                if (!StringUtils.isAvilible(getApplicationContext(), resource.getPackageName())) {
                    //没有安装此软件
                    mAdBean = resource;
                    showRecommendDialog();
                    break;
                }

                //如果都已经安装,则下载最新的第一个apk
                if (resource.isInstall() && i == response.size() - 1) {
                    mAdBean = response.get(0);
                    showRecommendDialog();
                    break;
                }
            }

            //如果是随机抽取
//            List<ADBean> noInstall = new ArrayList<>();
//            for (ADBean adBean : response) {
//                //过滤已经安装的apk
//                if(!adBean.isInstall()){
//                    noInstall.add(adBean);
//                }
//            }
//            int len = noInstall.size();
//
//            int index = new Random().nextInt(len);
//
//            mAdBean = noInstall.get(index) == null?new ADBean():noInstall.get(index);

        } else {
            //默认下载地址
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
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}