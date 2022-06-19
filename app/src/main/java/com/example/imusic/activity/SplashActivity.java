package com.example.imusic.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.example.imusic.R;
import com.example.imusic.adapter.SplashPagerAdapter;
import com.example.imusic.base.BaseActivity;
import com.example.imusic.fragment.dialogfrag.ScannerConfigDialog;
import com.example.imusic.model.MusicCountBean;
import com.example.imusic.service.LoadMusicDataService;
import com.example.imusic.util.ColorUtil;
import com.example.imusic.util.Constants;
import com.example.imusic.util.LogUtil;
import com.example.imusic.util.ServiceUtil;
import com.example.imusic.util.SpUtil;
import com.example.imusic.util.SystemUiVisibilityUtil;
import com.example.imusic.view.ProgressBtn;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lsp
 * Des：${TODO}
 * Time:2017/4/22 02:00
 */
public class SplashActivity
        extends BaseActivity {


    @BindView(R.id.tv_music_count)
    TextView mTvMusicCount;
    @BindView(R.id.music_count_pb)
    ProgressBtn mMusicLoadProgressBar;
    @BindView(R.id.vp_splash)
    ViewPager mVpSplash;
    private String mScanner;
    private boolean mIsFirstScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mBind = ButterKnife.bind(this);
        SystemUiVisibilityUtil.hideStatusBar(getWindow(), true);
        init();
    }

    private void init() {
        mScanner = getIntent().getStringExtra(Constants.SCANNER_MEDIA);
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(SpUtil.getPicUrlFlag(this, false));
        mVpSplash.setAdapter(splashPagerAdapter);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onResume() {
        super.onResume();
        String[] permissionArr = {Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE};
        AndPermission.with(this).runtime()
                .permission(permissionArr)
                .onGranted(permissions -> loadMusicData())
                .onDenied(permissions -> LogUtil.d(TAG, "没有读取和写入的权限!"))
                .start();
        mCompositeDisposable.add(mBus.toObserverable(String.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    // 首次安装，开启服务加载本地音乐，创建本地数据库。
                    if (!ServiceUtil.isServiceRunning(getApplicationContext(), Constants.LOAD_SERVICE_NAME)) {
                        startService(new Intent(getApplicationContext(), LoadMusicDataService.class));
                    }
                    updateLoadProgress();
                }));

    }

    private void loadMusicData() {
        if (mScanner == null) {
            mIsFirstScanner = true;
            // 是否是首次安装，本地数据库是否创建，等于 8 表示不是首次安装，数据库已经创建，直接进入MusicActivity。
            if (SpUtil.getLoadMusicFlag(this) == Constants.NUMBER_EIGHT) {
                countDownOpareton(true);
            } else {
                ScannerConfigDialog.newInstance(true).show(getSupportFragmentManager(), "auto_config");

//                // 首次安装，开启服务加载本地音乐，创建本地数据库。
//                if (!ServiceUtil.isServiceRunning(this, Constants.LOAD_SERVICE_NAME)) {
//                    startService(new Intent(this, LoadMusicDataService.class));
//                }
//                updataLoadProgress();
            }

        } else {
            // 手动扫描歌曲
            mIsFirstScanner = false;
            Intent intent = new Intent(this, LoadMusicDataService.class);
            intent.putExtra(Constants.SCANNER_MEDIA, Constants.SCANNER_MEDIA);
            startService(intent);
            updateLoadProgress();
        }


    }

    private void updateLoadProgress() {
        mTvMusicCount.setVisibility(View.VISIBLE);
        mMusicLoadProgressBar.setVisibility(View.VISIBLE);
        mCompositeDisposable.add(mBus.toObserverable(MusicCountBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicCountBean -> {
                    // 初次启动时size为所有音乐的数量，当手动扫描时为新增歌曲的数量。
                    int size = musicCountBean.getSize();
                    String str;
                    int currentCount = musicCountBean.getCurrentCount();
                    if (size > 0) {
                        mMusicLoadProgressBar.setMax(size);
                        str = "已经加载  " + currentCount + " 首本地音乐";
                        mTvMusicCount.setText(str);
                        mMusicLoadProgressBar.setProgress(currentCount);
                        if (currentCount == size) {
                            mTvMusicCount.setTextColor(ColorUtil.lyricsSelecte);
                            str = "本地音乐加载完成 -_-  共" + size + "首歌";
                            mTvMusicCount.setText(str);
                            // 初次扫描完成后进入MusicActivity
                            if (mIsFirstScanner) {
                                // 初次加载的标记
                                SpUtil.setLoadMusicFlag(SplashActivity.this, Constants.NUMBER_EIGHT);
                            } else {
                                // 手动扫描新增歌曲数量
                                str = "新增 " + size + " 首歌曲";
                                mTvMusicCount.setText(str);
                            }
                            countDownOpareton(mIsFirstScanner);
                        }
                    } else {
                        mTvMusicCount.setText(mIsFirstScanner ? "本地没有发现音乐,去下载歌曲后再来体验吧!" : "没有新增歌曲!");
                        countDownOpareton(false);
                    }
                }));
    }

    /**
     * 倒计时操作
     *
     * @param b true 表示初次安装，自动扫描完成后直接进入MusicActivity 。 false 表示手动扫描，完成后停在SplashActivity页面。
     */
    private void countDownOpareton(boolean b) {
        if (b) {
            startMusicActivity();
        } else {
            mTvMusicCount.setVisibility(View.GONE);
            mMusicLoadProgressBar.setVisibility(View.GONE);
        }
    }

    private void startMusicActivity() {
        SplashActivity.this.startActivity(new Intent(SplashActivity.this,
                MusicActivity.class));
        finish();
    }
}
