package com.example.imusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.jakewharton.rxbinding2.view.RxView;
import com.example.imusic.R;
import com.example.imusic.adapter.SearchPagerAdapter;
import com.example.imusic.base.BaseTansitionActivity;
import com.example.imusic.base.listener.OnFlowLayoutClickListener;
import com.example.imusic.base.listener.OnMusicItemClickListener;
import com.example.imusic.base.listener.TextChangedListener;
import com.example.imusic.model.MusicBean;
import com.example.imusic.model.MusicLyricBean;
import com.example.imusic.model.SearchCategoryBean;
import com.example.imusic.service.MusicPlayService;
import com.example.imusic.util.ColorUtil;
import com.example.imusic.util.Constants;
import com.example.imusic.util.FileUtil;
import com.example.imusic.util.LyricsUtil;
import com.example.imusic.util.SoftKeybordUtil;
import com.example.imusic.util.TitleArtistUtil;
import com.example.imusic.view.music.SmartisanControlBar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lsp
 */
public class SearchActivity extends BaseTansitionActivity implements OnMusicItemClickListener, OnFlowLayoutClickListener {
    @BindView(R.id.tv_search_cancel)
    TextView mTvSearchCancel;
    @BindView(R.id.iv_edit_clear)
    ImageView mIvEditClear;
    @BindView(R.id.edit_search)
    EditText mEditSearch;

    @BindView(R.id.vp_search)
    ViewPager2 mViewPager;

    @BindView(R.id.search_category_root)
    LinearLayout mSearchCategoryRoot;

    @BindView(R.id.tv_search_all)
    TextView mTvSearchAll;
    @BindView(R.id.tv_search_song)
    TextView mTvSearchSong;
    @BindView(R.id.tv_search_album)
    TextView mTvSearchAlbum;
    @BindView(R.id.tv_search_artist)
    TextView mTvSearchArtist;

    @BindView(R.id.smartisan_control_bar)
    SmartisanControlBar mSmartisanControlBar;
    private MusicBean mMusicBean;
    private MusicPlayService.AudioBinder audioBinder;
    private int lyricsFlag;
    private InputMethodManager mInputMethodManager;
    private Disposable mDisposableSoftKeyboard;
    private String mSearchCondition;
    private int currentCategoryPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mBind = ButterKnife.bind(this);
        init();
        initListener();
    }

    @Override
    protected void updateLyricsView(boolean lyricsOK, String downMsg) {
        if (lyricsOK) {
            updateLyric();
        }
    }

    private void init() {
        int pageType = getIntent().getIntExtra("pageType", 0);
        audioBinder = MusicActivity.getAudioBinder();
        mSmartisanControlBar.setPbColorAndPreBtnGone();
        SearchPagerAdapter pagerAdapter;
        if (pageType > Constants.NUMBER_ZERO) {
            mMusicBean = getIntent().getParcelableExtra("musicBean");
            mEditSearch.setText(mMusicBean.getArtist());
            mEditSearch.setSelection(mMusicBean.getArtist().length());
            mSearchCategoryRoot.setVisibility(View.VISIBLE);
            // ViewPager
            pagerAdapter = new SearchPagerAdapter(this, mMusicBean.getArtist());
            switchListCategory(3);
            setMusicInfo(mMusicBean);
            mIvEditClear.setVisibility(View.VISIBLE);
        } else {
            pagerAdapter = new SearchPagerAdapter(this, null);
            // 主动弹出键盘
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mDisposableSoftKeyboard = Observable.timer(50, TimeUnit.MILLISECONDS)
                    .subscribe(aLong -> SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 2, InputMethodManager.SHOW_FORCED));
        }

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switchListCategory(position);
            }
        });
    }

    private void initListener() {
        mEditSearch.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                mSearchCondition = s.toString();
                boolean conditionOK = !Constants.NULL_STRING.equals(mSearchCondition) && mSearchCondition.length() > 0;
                mBus.post(new SearchCategoryBean(conditionOK ? getDataFlag() : Constants.NUMBER_TEN, mSearchCondition));
                mIvEditClear.setVisibility(conditionOK ? View.VISIBLE : View.GONE);
                mSearchCategoryRoot.setVisibility(conditionOK ? View.VISIBLE : View.GONE);
            }
        });
        mSmartisanControlBar.setClickListener(clickFlag -> {
            switch (clickFlag) {
                case Constants.NUMBER_ONE:
                    audioBinder.updataFavorite();
                    checkCurrentSongIsFavorite(mMusicBean, null, mSmartisanControlBar);
                    break;
                case Constants.NUMBER_TWO:
                    audioBinder.playPre();
                    break;
                case Constants.NUMBER_THREE:
                    switchPlayState();
                    break;
                case Constants.NUMBER_FOUR:
                    audioBinder.playNext();
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioBinder != null) {
            mMusicBean = audioBinder.getMusicBean();
            setMusicInfo(mMusicBean);
            checkCurrentSongIsFavorite(mMusicBean, null, mSmartisanControlBar);
            mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
            updateLyric();
            setDuration();
        }
        mCompositeDisposable.add(RxView.clicks(mSmartisanControlBar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> startPlayActivity()));

    }


    @Override
    protected void updateCurrentPlayInfo(MusicBean musicItem) {
        mMusicBean = musicItem;
        SearchActivity.this.setMusicInfo(musicItem);
        mSmartisanControlBar.initAnimation();
        if (audioBinder != null) {
            setDuration();
            SearchActivity.this.checkCurrentSongIsFavorite(mMusicBean, null, mSmartisanControlBar);
            mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            updateLyric();
        }
    }

    @Override
    protected void updateCurrentPlayProgress() {
        if (audioBinder != null) {
            mSmartisanControlBar.setSongProgress(audioBinder.getProgress());
        }
    }

    private void setDuration() {
        int duration = audioBinder.getDuration();
        mSmartisanControlBar.setMaxProgress(duration);
    }

    private void setMusicInfo(MusicBean musicItem) {
        if (musicItem != null) {
            mSmartisanControlBar.setVisibility(View.VISIBLE);
            musicItem = TitleArtistUtil.getMusicBean(musicItem);
            mSmartisanControlBar.setSongName(musicItem.getTitle());
            mSmartisanControlBar.setSingerName(musicItem.getArtist());
            mSmartisanControlBar.setAlbulmUrl(FileUtil.getAlbumUrl(musicItem,1));
        }
        if (audioBinder != null) {
            mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            mSmartisanControlBar.initAnimation();
        } else {
            mSmartisanControlBar.setVisibility(View.GONE);
        }
    }


    private void switchListCategory(int position) {
        currentCategoryPosition = position;
        mViewPager.setCurrentItem(position, false);
        mBus.post(new SearchCategoryBean(getDataFlag(), mSearchCondition));
        switch (position) {
            case 0:
                setAllCategoryNotNormal();
                mTvSearchAll.setTextColor(ColorUtil.wihtle);
                mTvSearchAll.setBackgroundResource(R.drawable.btn_category_songname_down_selector);
                break;
            case 1:
                setAllCategoryNotNormal();
                mTvSearchSong.setTextColor(ColorUtil.wihtle);
                mTvSearchSong.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 2:
                setAllCategoryNotNormal();
                mTvSearchAlbum.setTextColor(ColorUtil.wihtle);
                mTvSearchAlbum.setBackgroundResource(R.drawable.btn_category_score_down_selector);
                break;
            case 3:
                setAllCategoryNotNormal();
                mTvSearchArtist.setBackgroundResource(R.drawable.btn_category_views_down_selector);
                mTvSearchArtist.setTextColor(ColorUtil.wihtle);
                break;
            default:
                break;
        }
    }

    private void setAllCategoryNotNormal() {
        mTvSearchAll.setTextColor(ColorUtil.textName);
        mTvSearchAll.setBackgroundResource(R.drawable.btn_category_songname_selector);
        mTvSearchSong.setTextColor(ColorUtil.textName);
        mTvSearchSong.setBackgroundResource(R.drawable.btn_category_score_selector);
        mTvSearchAlbum.setTextColor(ColorUtil.textName);
        mTvSearchAlbum.setBackgroundResource(R.drawable.btn_category_score_selector);
        mTvSearchArtist.setTextColor(ColorUtil.textName);
        mTvSearchArtist.setBackgroundResource(R.drawable.btn_category_views_selector);
    }

    @Override
    protected void refreshBtnAndNotify(int playStatus) {
        switch (playStatus) {
            case 0:
                mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
                mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
                break;
            case 1:
                checkCurrentSongIsFavorite(mMusicBean, null, mSmartisanControlBar);
                break;
            case 2:
                mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
                mSmartisanControlBar.animatorOnPause();
                break;
            default:
                break;
        }
    }

    private int getDataFlag() {
        return mSearchCondition != null ? 1 : currentCategoryPosition == 0 || currentCategoryPosition == 1 ?
                Constants.NUMBER_THREE : currentCategoryPosition == 2 ? 2 : currentCategoryPosition == 3 ? 1 : 4;
    }


    private void switchPlayState() {
        if (audioBinder.isPlaying()) {
            audioBinder.pause();
        } else {
            audioBinder.start();
        }
        mSmartisanControlBar.animatorOnResume(audioBinder.isPlaying());
        mSmartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
    }


    @OnClick({R.id.tv_search_cancel, R.id.iv_edit_clear,
            R.id.tv_search_all, R.id.tv_search_song, R.id.tv_search_album, R.id.tv_search_artist})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_search_cancel:
                SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 1, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                finish();

                break;
            case R.id.iv_edit_clear:
                mEditSearch.setText(null);
                findViewById(R.id.search_category_root).setVisibility(View.GONE);
                mBus.post(new SearchCategoryBean(Constants.NUMBER_NINE, null));
                break;
            case R.id.tv_search_all:
                switchListCategory(0);
                break;
            case R.id.tv_search_song:
                switchListCategory(1);
                break;
            case R.id.tv_search_album:
                switchListCategory(2);
                break;
            case R.id.tv_search_artist:
                switchListCategory(3);
                break;
        }
    }

    @Override
    public void startMusicServiceFlag(int position, int sortFlag, int dataFlag, String queryFlag) {
        Intent intent = new Intent(this, MusicPlayService.class);
        intent.putExtra("sortFlag", sortFlag);
        intent.putExtra("dataFlag", dataFlag);
        intent.putExtra("queryFlag", queryFlag);
        intent.putExtra("position", position);
        startService(intent);
    }

    @Override
    public void startMusicService(int position) {

    }

    @Override
    public void onOpenMusicPlayDialogFag() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartisanControlBar.animatorOnPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposableSoftKeyboard != null) {
            mDisposableSoftKeyboard.dispose();
            mDisposableSoftKeyboard = null;
        }
        if (audioBinder != null) {
            audioBinder = null;
        }
    }

    private void updateLyric() {
        List<MusicLyricBean> lyricList = LyricsUtil.getLyricList(mMusicBean);
        disposableQqLyric();
        if (mQqLyricsDisposable == null) {
            mQqLyricsDisposable = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(musicBeanList -> {
                        if (lyricList.size() > 1 && lyricsFlag < lyricList.size()) {
                            //通过集合，播放过的歌词就从集合中删除
                            MusicLyricBean lyrBean = lyricList.get(lyricsFlag);
                            String content = lyrBean.getContent();
                            int progress = audioBinder.getProgress();
                            int startTime = lyrBean.getStartTime();
                            if (progress > startTime) {
                                mSmartisanControlBar.setSingerName(content);
                                lyricsFlag++;
                            }
                        }
                    });
        }

    }

    @Override
    public void click(String songName) {
        mEditSearch.setText(songName);
        mEditSearch.setSelection(songName.length());

    }

}
