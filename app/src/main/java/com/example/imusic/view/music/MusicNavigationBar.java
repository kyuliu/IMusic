package com.example.imusic.view.music;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.imusic.R;
import com.example.imusic.util.ColorUtil;
import com.example.imusic.util.Constants;


/**
 * @ Name:   NavigationBar
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/11/ 0:18
 * @ Des:     底部的导航Tab
 * @author Luoshipeng
 */
public class MusicNavigationBar extends LinearLayout implements View.OnClickListener {
    ImageView mMusicBarPlaylistIv;
    TextView mMusicBarPlaylistTv;
    LinearLayout mMusicBarPlaylist;
    ImageView mMusicBarArtisanlistIv;
    TextView mMusicBarArtisanlistTv;
    LinearLayout mMusicBarArtisanlist;
    ImageView mMusicBarSonglistIv;
    TextView mMusicBarSonglistTv;
    LinearLayout mMusicBarSonglist;
    ImageView mMusicBarAlbumlistIv;
    TextView mMusicBarAlbumlistTv;
    LinearLayout mMusicBarAlbumlist;
    ImageView mMusicBarStylelistIv;
    TextView mMusicBarStylelistTv;
    LinearLayout mMusicBarAboutLl;
    private final int mNormalTabbarColor = Color.parseColor("#939396");

    public MusicNavigationBar(Context context) {
        super(context);
        initView();
    }


    public MusicNavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.music_bar_playlist:
                switchMusicTabbar(Constants.NUMBER_ZERO);
                break;
            case R.id.music_bar_artisanlist:
                switchMusicTabbar(Constants.NUMBER_ONE);
                break;
            case R.id.music_bar_songlist:
                switchMusicTabbar(Constants.NUMBER_TWO);
                break;
            case R.id.music_bar_albumlist:
                switchMusicTabbar(Constants.NUMBER_THREE);
                break;
            case R.id.music_bar_about:
                switchMusicTabbar(Constants.NUMBER_FOUR);
                break;
            default:
                break;
        }
    }

    public void switchMusicTabbar(int flag) {
        setAllTabbarNotPressed(flag);
        switch (flag) {
            case Constants.NUMBER_ZERO:
                mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_selector);
                mMusicBarPlaylistTv.setTextColor(ColorUtil.musicbarTvDown);
                mMusicBarPlaylist.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                break;
            case Constants.NUMBER_ONE:
                mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_selector);
                mMusicBarArtisanlistTv.setTextColor(ColorUtil.musicbarTvDown);
                mMusicBarArtisanlist.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                break;
            case Constants.NUMBER_TWO:
                mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_selector);
                mMusicBarSonglistTv.setTextColor(ColorUtil.musicbarTvDown);
                mMusicBarSonglist.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                break;
            case Constants.NUMBER_THREE:
                mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_selector);
                mMusicBarAlbumlist.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                mMusicBarAlbumlistTv.setTextColor(ColorUtil.musicbarTvDown);
                break;
            case Constants.NUMBER_FOUR:
                mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_selector);
                mMusicBarStylelistTv.setTextColor(ColorUtil.musicbarTvDown);
                mMusicBarAboutLl.setBackground(getResources().getDrawable(R.drawable.tabbar_bg_down));
                break;
            default:
                break;
        }

    }

    /**
     * 将Tabbar全部置于未选种状态
     *
     * @param flag 将ViewPager切换到选中的Tag
     */
    private void setAllTabbarNotPressed(int flag) {
        if (mBarSelecteListener != null) {
            mBarSelecteListener.currentFlag(flag);
        }
        mMusicBarPlaylist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarPlaylistIv.setBackgroundResource(R.drawable.tabbar_playlist_down_selector);
        mMusicBarPlaylistTv.setTextColor(mNormalTabbarColor);

        mMusicBarArtisanlist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarArtisanlistIv.setBackgroundResource(R.drawable.tabbar_artisanlist_down_selector);
        mMusicBarArtisanlistTv.setTextColor(mNormalTabbarColor);

        mMusicBarSonglist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarSonglistIv.setBackgroundResource(R.drawable.tabbar_songlist_down_selector);
        mMusicBarSonglistTv.setTextColor(mNormalTabbarColor);

        mMusicBarAlbumlist.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarAlbumlistIv.setBackgroundResource(R.drawable.tabbar_albumlist_down_selector);
        mMusicBarAlbumlistTv.setTextColor(mNormalTabbarColor);

        mMusicBarAboutLl.setBackgroundColor(ColorUtil.wihtle);
        mMusicBarStylelistIv.setBackgroundResource(R.drawable.tabbar_stylelist_down_selector);
        mMusicBarStylelistTv.setTextColor(mNormalTabbarColor);
    }

    private void initListener() {
        mMusicBarAboutLl.setOnClickListener(this);
        mMusicBarPlaylist.setOnClickListener(this);
        mMusicBarSonglist.setOnClickListener(this);
        mMusicBarAlbumlist.setOnClickListener(this);
        mMusicBarArtisanlist.setOnClickListener(this);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.music_navigationbar, this, true);

        mMusicBarPlaylistIv = findViewById(R.id.music_bar_playlist_iv);
        mMusicBarPlaylistTv = findViewById(R.id.music_bar_playlist_tv);
        mMusicBarPlaylist = findViewById(R.id.music_bar_playlist);

        mMusicBarArtisanlistIv = findViewById(R.id.music_bar_artisanlist_iv);
        mMusicBarArtisanlistTv = findViewById(R.id.music_bar_artisanlist_tv);
        mMusicBarArtisanlist = findViewById(R.id.music_bar_artisanlist);

        mMusicBarSonglistIv = findViewById(R.id.music_bar_songlist_iv);
        mMusicBarSonglistTv = findViewById(R.id.music_bar_songlist_tv);
        mMusicBarSonglist = findViewById(R.id.music_bar_songlist);

        mMusicBarAlbumlistIv = findViewById(R.id.music_bar_albumlist_iv);
        mMusicBarAlbumlistTv = findViewById(R.id.music_bar_albumlist_tv);
        mMusicBarAlbumlist = findViewById(R.id.music_bar_albumlist);

        mMusicBarStylelistIv = findViewById(R.id.music_bar_stylelist_iv);
        mMusicBarStylelistTv = findViewById(R.id.music_bar_stylelist_tv);
        mMusicBarAboutLl = findViewById(R.id.music_bar_about);
        initListener();
    }

    private OnNavigationBarSelecteListener mBarSelecteListener;

    public void setOnNavigationbarListener(OnNavigationBarSelecteListener selecteListener) {
        mBarSelecteListener = selecteListener;
    }

    public interface OnNavigationBarSelecteListener {
        void currentFlag(int currentSelecteFlag);

    }
}
