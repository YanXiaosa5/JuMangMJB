package com.baitu.fangyuan;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.baitu.fangyuan.model.ADBean;
import com.baitu.fangyuan.utils.GsonUtils;
import com.baitu.fangyuan.utils.SharePreferenceUtils;
import com.baitu.fangyuan.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

/**
 * @date: 2018/9/26
 * 每次都会切换,下载后排除已经下载的项
 */
public class NewMainActivity extends BaseActivity {

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
    private LinkedHashMap<String, List<ADBean>> dataMap = new LinkedHashMap();

    /**
     * 子数据索引
     */
    private int childIndex = 0;

    /**
     * 是否是默认apk
     */
    private boolean isDefault;

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

        //推荐名称
//        TextView tv_tuijian_title = (TextView) dialog.findViewById(R.id.tv_tuijian_title);

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
                if (!cb_can_choice.isChecked()) {
                    sureClose();
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_can_choice.isChecked()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    sureCancel();
                    finish();
                    return;
                }

                isClickOpen = true;
                if (!mAdBean.isDownLoad()) {
                    PopLoading.getInstance().setText("加载中...").show(NewMainActivity.this);
                    if (mAdBean != null) {
                        openOtherApp(mAdBean);
                    }
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if(mAdBean.getDownLoadFile() != null) {
                        openFile(getApplicationContext(), mAdBean.getDownLoadFile());
                        finish();
                        System.out.println("已经下载finish");
                    }else{
                        openOtherApp(mAdBean);
                    }
//                    openFile(NewMainActivity.this, file);
                }
            }
        });

        if (!dialog.isShowing())
            dialog.show();
    }

    /**
     * 直接点击关闭
     */
    public void cancel() {
        MobclickAgent.onEvent(getApplicationContext(), "cancel", MyApplication.CHANNEL_NAME);
    }

    /**
     * 未选中复选框,点击确定
     */
    public void sureCancel() {
        MobclickAgent.onEvent(getApplicationContext(), "sureCancel", MyApplication.CHANNEL_NAME);
    }

    /**
     * 未选中复选框,点击关闭
     */
    public void sureClose() {
        MobclickAgent.onEvent(getApplicationContext(), "sureClose", MyApplication.CHANNEL_NAME);
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
        if (!isDefault) {
            getShareIndex();
            List<String> keyStrings = filterKey(mAdBeans);
            choiceOne(keyStrings);
        }
    }

    /**
     * 获取上一次缓存的数据
     */
    public void getShareIndex() {
        //获取上一次缓存的索引
        childIndex = SharePreferenceUtils.getIndex(2, this);
    }

    /**
     * 下载
     * yxs
     *
     * @param adBean
     */
    public void startDownLoad(ADBean adBean) {
        MobclickAgent.onEvent(getApplicationContext(), "startDownLoad", MyApplication.CHANNEL_NAME);
        String url = adBean.getDownloadUrl() == null ? "https://cdn.138pool.com/apk/huajian_AZRONG_su3.apk" : adBean.getDownloadUrl();
        isDownloading = true;
        PopLoading.getInstance().show(this);
        String[] split = url.split("/");
        String path = getCacheDir().getPath();
        String apkName = split == null ? "fanhua.apk" : split[split.length - 1];

        file = new File(path + File.separator + apkName);
        if (file.exists()) {
            isDownloading = false;
            openFile(this, file);
        } else {
            OkHttpUtils.get().url(url).build().execute(new FileCallBack(path, apkName) {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Toast.makeText(NewMainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    StringUtils.saveFile(GsonUtils.toJson(e==null?"":e),"下载失败");
                    isDownloading = false;
                }

                @Override
                public void onResponse(File response, int id) {
                    mAdBean.setDownLoad(true);
                    mAdBean.setDownLoadFile(response);
                    isDownloading = false;
                    if (isClickOpen) {
                        if (dialog != null && dialog.isShowing() && !isFinishing()) {
                            dialog.dismiss();
                        }
                        finish();
                        System.out.println("下载完成后跳转安装finish");
                        openFile(NewMainActivity.this, response);
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

        MobclickAgent.onEvent(getApplicationContext(), "openFile", MyApplication.CHANNEL_NAME);

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
        MobclickAgent.onEvent(getApplicationContext(), "requestAD", MyApplication.CHANNEL_NAME);
        ApiUrl.adList(new ResultCallback<List<ADBean>>(this) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onSuccess(List<ADBean> response) {
                super.onSuccess(response);
                System.out.println("获取广告数据:" + GsonUtils.toJson(response));

                mAdBeans = response;
                List<String> keyStrings = filterKey(mAdBeans);
                if (keyStrings != null && keyStrings.size() > 0) {
                    choiceOne(keyStrings);
                    isDefault = false;
                } else {
                    defaultADbean();
                }
            }
        });
    }

    /**
     * 无数据默认
     */
    public void defaultADbean() {
        isDefault = true;
        mAdBean = defaultBean();
        showRecommendDialog();
    }

    /**
     * 生成默认的推荐
     * @return
     */
    public ADBean defaultBean() {
        String downloadUrl = "https://cdn.138pool.com/apk/huajian_AZRONG_su3.apk";
        String adIcon = "http://huajian.h9a9.top/lmmy/data/star/5/4.jpg";
        ADBean adBean = new ADBean();
        adBean.setAdIcon(adIcon);
        adBean.setAdName("美女直播");
        adBean.setChecked(1);
        adBean.setEnableCheck(0);
        adBean.setDownloadUrl(downloadUrl);
        return adBean;
    }

    /**
     * 从元数据中取出所有的key值,进行分类,以便于map添加分组数据(没有关联索引)
     *
     * @param adBeans
     */
    public List<String> filterKey(List<ADBean> adBeans) {

        final ArrayList<String> keyList = new ArrayList<>();

        //如果数据源为空,使用默认数据
        if(adBeans == null || adBeans.size() <= 0){
            keyList.add("com.yr.huajian");
            dataMap.clear();
            ArrayList<ADBean> defaultBeanList = new ArrayList<>();
            defaultBeanList.add(defaultBean());
            dataMap.put("com.yr.huajian", defaultBeanList);
            return keyList;
        }

        for (int i = 0; i < adBeans.size(); i++) {
            ADBean adBean = adBeans.get(i);
            if (!StringUtils.isAvilible(getApplicationContext(), adBean.getPackageName())) {
                //只保留一个而且是第一个检测未安装的包名,如果安装则不添加
                keyList.add(adBean.getPackageName());
                break;
            }
        }

        //如果全部安装了,那么默认推荐第一个
        if (keyList.size() <= 0) {
            if(adBeans.get(0).getPackageName() != null) {
                keyList.add(adBeans.get(0).getPackageName());
            }else{
                keyList.add("com.yr.huajian");
                dataMap.clear();
                ArrayList<ADBean> defaultBeanList = new ArrayList<>();
                defaultBeanList.add(defaultBean());
                dataMap.put("com.yr.huajian", defaultBeanList);
                return keyList;
            }
        }

        //获取保存的未安装的包名
        String key = keyList.get(0);//key值
        List<ADBean> categoryList = new ArrayList<>();

        for (int j = 0; j < adBeans.size(); j++) {//元数据
            ADBean adBean = adBeans.get(j);//每一个广告信息
            String packageName = adBean.getPackageName();
            if (packageName.equals(key)) {
                categoryList.add(adBean);
            }
        }
        dataMap.put(key, categoryList);

        return keyList;
    }

    /**
     * 顺序选择(关联索引)
     * @param keyList
     */
    public void choiceOne(List<String> keyList) {

        //判断索引是否超过数据的长度
        if (childIndex > dataMap.get(keyList.get(0)).size() - 1) {
            childIndex = 0;
        }

        List<ADBean> adBeans = dataMap.get(keyList.get(0));

        if (adBeans != null && adBeans.size() > 0) {

            String key = keyList.get(0);
            mAdBean = dataMap.get(key).get(childIndex);
            //在这里就要缓存索引，避免用户杀死进程，无法缓存
            shareIndex();
            showRecommendDialog();
        } else {
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
    public void shareIndex() {
        SharePreferenceUtils.save(this, 0, childIndex);
    }

    @Override
    public void finish() {

        if(!isDefault){
            childIndex++;
            shareIndex();
        }
        super.finish();
    }
}