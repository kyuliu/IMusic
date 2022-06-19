package com.example.imusic.view.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.imusic.R;
import com.example.imusic.activity.PlayListActivity;
import com.example.imusic.adapter.DetailsViewAdapter;
import com.example.imusic.base.listener.OnMusicItemClickListener;
import com.example.imusic.fragment.dialogfrag.AlbumDetailDialogFragment;
import com.example.imusic.fragment.dialogfrag.RelaxDialogFragment;
import com.example.imusic.fragment.dialogfrag.PreviewBigPicDialogFragment;
import com.example.imusic.model.AlbumInfo;
import com.example.imusic.model.ArtistInfo;
import com.example.imusic.model.MusicBean;
import com.example.imusic.network.QqMusicRemote;
import com.example.imusic.util.Constants;
import com.example.imusic.util.ImageUitl;
import com.example.imusic.util.LogUtil;
import com.example.imusic.util.RandomUtil;
import com.example.imusic.util.SpUtil;
import com.example.imusic.util.StringUtil;
import com.example.imusic.view.MusicScrollView;
import com.example.imusic.view.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Des：${将详情页面封装到一个Viwe里面，方便多个页面使用}
 * Time:2017/9/10 00:43
 *
 * @author Stran
 */

public class DetailsView
        extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "====" + DetailsView.class.getSimpleName() + "    ";
    private RecyclerView mRecyclerView;
    private ImageView mIvArtistAlbumDetails;
    private TextView mTvArtistAlbumDetailsTitle;
    private TextView mTvArtistAlbumDetailsArtist;
    private TextView mTvArtistAlbumDetailsDate;
    private ImageView mIvDetailsAddToList;
    private ImageView mIvDetailsAddToPlayList;
    private LinearLayout mLlAlbumDetailsPlayaLl;
    private LinearLayout mLlAlbumDetailsRandomPlay;
    private int mDataFlag;
    private int mListSize;
    private String mQueryFlag;
    private FragmentManager mFragmentManager;
    private Long mAlbumId;
    private List<MusicBean> mMusicList;
    private MusicScrollView mMusicScrollView;
    private LinearLayout mSuspensionLl;
    private String mArtist;
    private int mPicType;

    public void setDataFlag(FragmentManager fragmentManager, int listSize, String queryFlag, int dataFlag) {
        this.mFragmentManager = fragmentManager;
        this.mDataFlag = dataFlag;
        this.mQueryFlag = queryFlag;
        this.mListSize = listSize;
    }

    public DetailsView(Context context) {
        super(context);
        initView();
        initListener();
    }


    public DetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initListener();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
//        LogUtil.d("==========++== 详情显示  " + visibility);

    }

    /**
     * //设置列表的适配器
     *
     * @param dataType 将数据 强转成指定的类型  1 ： ArtistInfo   2 ： AlbumInfo
     * @param bean     d
     * @param adapter  d
     */
    public void setAdapter(int dataType, Object bean, DetailsViewAdapter adapter) {
        initData(dataType, bean);
        mMusicList = adapter.getData();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_artist_albumm_details_title:
                String albulmUrl = StringUtil.getAlbum(mPicType, mAlbumId, mArtist);
                openAlbumDetail(albulmUrl, mArtist);
                break;
            case R.id.iv_artist_albumm_details:
                LogUtil.d(TAG, " pictype " + mPicType);
                String albumUrl = StringUtil.getAlbum(mPicType, mAlbumId, mArtist);
                PreviewBigPicDialogFragment.newInstance(albumUrl)
                        .show(mFragmentManager, "album");
                break;
            case R.id.iv_details_add_to_list:
                startPlayListActivity();
                break;
            case R.id.iv_details_add_to_play_list:
                LogUtil.d(TAG,"=================添加到当前播放列表");
                break;
            case R.id.ll_album_details_playall:
                startMusic(Constants.NUMBER_ZERO);
                break;
            case R.id.ll_album_details_random_play:
                startMusic(RandomUtil.getRandomPostion(mListSize));
                break;
            default:
                break;

        }
    }

    private void openAlbumDetail(String albulmUrl, String albumName) {
        LogUtil.d(TAG, "显示专辑详情  " + albumName);
        AlbumDetailDialogFragment.newInstance(albulmUrl, albumName).show(mFragmentManager, "album detail");
    }

    protected void startPlayListActivity() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (MusicBean musicBean : mMusicList) {
            arrayList.add(musicBean.getTitle());
        }
        Intent intent = new Intent(getContext(), PlayListActivity.class);
        intent.putStringArrayListExtra(Constants.ADD_TO_LIST, arrayList);
        getContext().startActivity(intent);
    }

    private void startMusic(int startPosition) {
        if (getContext() instanceof OnMusicItemClickListener) {
            SpUtil.setSortFlag(getContext(), Constants.NUMBER_TEN);
            ((OnMusicItemClickListener) getContext()).startMusicServiceFlag(startPosition, Constants.NUMBER_TEN, mDataFlag, mQueryFlag);
        }
    }


    /**
     * 根据dataType将bean转换具体的数据类型
     *
     * @param dataType d
     * @param bean     b
     */
    private void initData(int dataType, Object bean) {
        mPicType = dataType;
        if (dataType == Constants.NUMBER_ONE) {
            ArtistInfo info = (ArtistInfo) bean;
            mArtist = info.getArtist();
            mAlbumId = info.getAlbumId();
            setMusicInfo(dataType, info.getAlbumName(), info.getArtist(), mAlbumId, info.getYear());

        } else if (dataType == Constants.NUMBER_TWO) {
            AlbumInfo info = (AlbumInfo) bean;
            mAlbumId = info.getAlbumId();
            mArtist = info.getAlbumName();
            setMusicInfo(dataType, info.getAlbumName(), info.getArtist(), mAlbumId, info.getYear());

        }

    }


    private void setMusicInfo(int dataType, String albumName, String artist, long albumId, int issueYear) {
        mTvArtistAlbumDetailsTitle.setText(albumName);
        mTvArtistAlbumDetailsArtist.setText(artist);
        ImageUitl.loadPic((Activity) getContext(), StringUtil.getAlbum(dataType, albumId, artist), mIvArtistAlbumDetails, R.drawable.noalbumcover_220, isSuccess -> {
            if (!isSuccess) {
                if (dataType == 1) {
                    QqMusicRemote.getArtistImg(getContext(), artist, url -> {
                        if (!url.isEmpty()) {
                            Glide.with(getContext()).load(url).placeholder(R.drawable.noalbumcover_220).error(R.drawable.noalbumcover_220).into(mIvArtistAlbumDetails);
                        }
                    });

                } else {
                    QqMusicRemote.getAlbumImg(getContext(), albumName, url -> {
                        if (!url.isEmpty()) {
                            Glide.with(DetailsView.this.getContext()).load(url).placeholder(R.drawable.noalbumcover_220).error(R.drawable.noalbumcover_220).into(mIvArtistAlbumDetails);
                        }
                    });

                }
            }

        });


        if (issueYear != Constants.NUMBER_ZERO) {
            String year = String.valueOf(issueYear);
            mTvArtistAlbumDetailsDate.setText(year);
        }

    }

    private void initListener() {
        mIvDetailsAddToList.setOnClickListener(this);
        mIvDetailsAddToPlayList.setOnClickListener(this);
        mLlAlbumDetailsPlayaLl.setOnClickListener(this);
        mLlAlbumDetailsRandomPlay.setOnClickListener(this);
        mIvArtistAlbumDetails.setOnClickListener(this);
        mTvArtistAlbumDetailsTitle.setOnClickListener(this);
        mIvArtistAlbumDetails.setOnLongClickListener(v -> {
            RelaxDialogFragment.newInstance().show(mFragmentManager, "girlsDialog");
            return true;
        });
    }


    private void initView() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.details_fragment, this, true);
        mMusicScrollView = findViewById(R.id.detail_root);
        mSuspensionLl = findViewById(R.id.ll_play);
        mRecyclerView = findViewById(R.id.rv_artist_album_details);
        mIvArtistAlbumDetails = findViewById(R.id.iv_artist_albumm_details);
        mTvArtistAlbumDetailsTitle = findViewById(R.id.tv_artist_albumm_details_title);
        mTvArtistAlbumDetailsArtist = findViewById(R.id.tv_artist_albumm_details_artist);
        mTvArtistAlbumDetailsDate = findViewById(R.id.tv_artist_albumm_details_date);
        mIvDetailsAddToList = findViewById(R.id.iv_details_add_to_list);
        mIvDetailsAddToPlayList = findViewById(R.id.iv_details_add_to_play_list);
        mLlAlbumDetailsPlayaLl = findViewById(R.id.ll_album_details_playall);
        mLlAlbumDetailsRandomPlay = findViewById(R.id.ll_album_details_random_play);
        mRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.shape_item_decoration)));
        mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
        mRecyclerView.addItemDecoration(divider);
    }

    public void setSuspension() {
        mMusicScrollView.resetHeight(mSuspensionLl, mRecyclerView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}







