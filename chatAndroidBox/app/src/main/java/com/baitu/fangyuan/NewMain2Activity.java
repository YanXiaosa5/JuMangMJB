package com.baitu.fangyuan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baitu.fangyuan.OkHttpUtils.OkHttpUtils;
import com.baitu.fangyuan.OkHttpUtils.ResultCallback;
import com.baitu.fangyuan.OkHttpUtils.callback.FileCallBack;
import com.baitu.fangyuan.model.ADBean;
import com.baitu.fangyuan.utils.GsonUtils;
import com.baitu.fangyuan.utils.ScreenUtil;
import com.baitu.fangyuan.utils.ScreenUtils;
import com.baitu.fangyuan.utils.StringUtils;
import com.baitu.fangyuan.utils.ViewUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

/**
 * 轮播模式(大图)
 */
public class NewMain2Activity extends Activity {

    /**
     * banner
     */
    private Banner banner;

    /**
     * 下载按钮
     */
    private Button btn_download;

    /**
     * 广告文本
     */
    private TextView tv_adname;

    /**
     * 分组数据,根据包名分组
     */
    private LinkedHashMap<String, List<ADBean>> mCategoryList = new LinkedHashMap<>();

    /**
     * 根据地址保存每一条广告数据
     */
    private LinkedHashMap<String, ADBean> mUrl2ADBean = new LinkedHashMap<>();

    /**
     * 广告数据
     */
    private List<ADBean> mADBeans;

    /**
     * 是否正在下载
     */
    private boolean isDownloading = false;

    /**
     * 当前下载的文件
     */
    private File file;

    /**
     * 全局的广告信息
     */
    private ADBean mAdBean;

    /**
     * 字体
     */
    private Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main2);
        btn_download = (Button) findViewById(R.id.btn_download);
        tv_adname = (TextView) findViewById(R.id.tv_adname);

        ViewUtils.setViewSize(btn_download, ScreenUtils.getScreenWidth(getApplicationContext())/2,150);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestAD();
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDownloading) {
                    startDownLoad(mAdBean);
                }
            }
        });
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/yansheng.TTF");
        tv_adname.setTypeface(typeFace);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //抽取key
        List<String> strings = filterKey(mADBeans);
        String key = strings.get(0);
        bannerData(mCategoryList.get(key));
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
                if (response != null) {
                    mADBeans = response;
                } else {
                    mADBeans = new ArrayList<>();
                    mADBeans.add(defaultBean());
                }

                //抽取key
                List<String> strings = filterKey(mADBeans);
                String key = strings.get(0);
                bannerData(mCategoryList.get(key));
            }
        });
    }

    /**
     * 生成默认的推荐
     *
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
        adBean.setPackageName("com.yr.huajian");
        return adBean;
    }

    /**
     * 过滤key
     *
     * @param adBeans
     * @return
     */
    public List<String> filterKey(List<ADBean> adBeans) {

        final ArrayList<String> keyList = new ArrayList<>();

        //如果数据源为空,使用默认数据
        if (adBeans == null || adBeans.size() <= 0) {
            keyList.add("com.yr.huajian");
            mCategoryList.clear();
            ArrayList<ADBean> defaultBeanList = new ArrayList<>();
            defaultBeanList.add(defaultBean());
            mCategoryList.put("com.yr.huajian", defaultBeanList);
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
            if (adBeans.get(0).getPackageName() != null) {
                keyList.add(adBeans.get(0).getPackageName());
            } else {
                keyList.add("com.yr.huajian");
                mCategoryList.clear();
                ArrayList<ADBean> defaultBeanList = new ArrayList<>();
                defaultBeanList.add(defaultBean());
                mCategoryList.put("com.yr.huajian", defaultBeanList);
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
        mCategoryList.put(key, categoryList);

        return keyList;
    }

    /**
     * 抽取banner数据
     *
     * @param mADBeans
     */
    private void bannerData(List<ADBean> mADBeans) {
        //抽取图片数据
        List<String> images = new ArrayList<>();
        for (ADBean adBean : mADBeans) {
            images.add(adBean.getAdIcon());
            mUrl2ADBean.put(adBean.getAdIcon(), adBean);
        }
        initBanner(images);
        mAdBean = mADBeans.get(0);
    }

    /**
     * 初始化banner
     */
    private void initBanner(final List<String> images) {
        banner = (Banner) findViewById(R.id.banner);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        tv_adname.setText(mUrl2ADBean.get(images.get(0) == null ? "" : images.get(0)) == null ? defaultBean().getAdName() : mUrl2ADBean.get(images.get(0) == null ? "" : images.get(0)).getAdName());
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("当前位置" + position);
                mAdBean = mUrl2ADBean.get(images.get(position));
                tv_adname.setText(mAdBean.getAdName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 开启下载
     *
     * @param adBean
     */
    public void startDownLoad(final ADBean adBean) {
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
            finish();
        } else {
            OkHttpUtils.get().url(url).build().execute(new FileCallBack(path, apkName) {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                    StringUtils.saveFile(GsonUtils.toJson(e == null ? "" : e), "下载失败");
                    isDownloading = false;
                }

                @Override
                public void onResponse(File response, int id) {
                    PopLoading.getInstance().hide(NewMain2Activity.this);
                    adBean.setDownLoad(true);
                    adBean.setDownLoadFile(response);
                    isDownloading = false;
                    openFile(NewMain2Activity.this, response);
                    System.out.println("下载完成后跳转安装finish");
                    finish();
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

    public class GlideImageLoader extends ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(context).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.default_icon).into(imageView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        PopLoading.getInstance().hide(getApplicationContext());
        if (banner != null)
            banner.stopAutoPlay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (banner != null)
            banner.startAutoPlay();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //被系统回收,关闭所有弹出的dialog
        PopLoading.getInstance().hide(this);
    }

}
