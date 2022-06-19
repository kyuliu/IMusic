package com.example.imusic.fragment;

import android.view.View;

import com.example.imusic.R;
import com.example.imusic.adapter.ArtistAdapter;
import com.example.imusic.adapter.DetailsViewAdapter;
import com.example.imusic.base.BaseLazyFragment;
import com.example.imusic.fragment.dialogfrag.MoreMenuBottomDialog;
import com.example.imusic.model.ArtistInfo;
import com.example.imusic.model.MusicBean;
import com.example.imusic.model.greendao.MusicBeanDao;
import com.example.imusic.util.Constants;
import com.example.imusic.util.LogUtil;
import com.example.imusic.util.MusicListUtil;
import com.example.imusic.view.music.DetailsView;
import com.example.imusic.view.music.MusicToolBar;
import com.example.imusic.view.music.MusicView;

import java.util.List;

import butterknife.BindView;


/**
 * @项目名： ArtisanMusic
 * @包名： com.example.imusic.artisanlist
 * @文件名: ArtistFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 23:49
 * @描述： {TODO}
 */

public class ArtistFragment extends BaseLazyFragment {

    @BindView(R.id.music_toolbar_list)
    MusicToolBar mMusicToolBar;
    @BindView(R.id.artist_music_view)
    MusicView mMusicView;
    @BindView(R.id.details_view)
    DetailsView mDetailsView;
    private ArtistAdapter mAdapter;
    private boolean isShowDetailsView = false;
    private DetailsViewAdapter mDetailsAdapter;
    private List<MusicBean> mDetailList;
    private String mTempTitle;


    @Override
    protected boolean getIsOpenDetail() {
        return isShowDetailsView;
    }

    @Override
    protected void initView(View view) {
        mMusicToolBar.setToolbarTitle(isShowDetailsView ? mTempTitle : getString(R.string.music_artisan));
        mMusicToolBar.setTvEditVisibility(isShowDetailsView);
    }

    @Override
    public void onResume() {
        super.onResume();
        initListener();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.artisan_list_fragment;
    }

    private void initListener() {
        mAdapter.setItemListener((bean, position, bean2) -> ArtistFragment.this.openDetailsView(bean));
        mMusicToolBar.setClickListener(new MusicToolBar.OnToolbarClickListener() {
            @Override
            public void clickEdit() {
                if (isShowDetailsView) {
                    openDetailsView(null);
                }
            }

            @Override
            public void switchMusicControlBar() {
                switchControlBar();
            }

            @Override
            public void clickDelete() {
            }
        });
    }


    @Override
    protected void initData() {
        List<MusicBean> musicBeans = mMusicBeanDao.queryBuilder().list();
        List<ArtistInfo> artistList = MusicListUtil.getArtistList(musicBeans);
        mAdapter = new ArtistAdapter(artistList);
        mMusicView.setAdapter(getActivity(), Constants.NUMBER_TWO, true, mAdapter);

    }

    private void openDetailsView(ArtistInfo artistInfo) {
        if (!isShowDetailsView) {
            if (artistInfo != null) {
                mTempTitle = artistInfo.getAlbumName();
                mDetailList = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(artistInfo.getArtist())).build().list();
                // DetailsView播放音乐需要的参数
                mDetailsView.setDataFlag(mFragmentManager, mDetailList.size(), artistInfo.getArtist(), Constants.NUMBER_ONE);
                mDetailsAdapter = new DetailsViewAdapter(mActivity, mDetailList, Constants.NUMBER_ONE);
                mDetailsView.setAdapter(Constants.NUMBER_ONE, artistInfo, mDetailsAdapter);
                mDetailsAdapter.setOnItemMenuListener((int position, MusicBean musicBean) ->
                        MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(mActivity));
                mDetailsView.setSuspension();
                interceptBackEvent(Constants.NUMBER_NINE);
                mMusicToolBar.setTvEditText(R.string.music_artisan);
            }
        }
        mDetailsView.setVisibility(isShowDetailsView ? View.GONE : View.VISIBLE);
        mMusicToolBar.setToolbarTitle(isShowDetailsView ? getString(R.string.music_artisan) : mTempTitle);
        isShowDetailsView = !isShowDetailsView;
        mMusicToolBar.setTvEditVisibility(isShowDetailsView);
    }

    @Override
    protected void deleteItem(int musicPosition) {
        super.deleteItem(musicPosition);
        if (mDetailList != null && mDetailsAdapter != null) {
            mDetailList.remove(musicPosition);
            mDetailsAdapter.setData(mDetailList);
        }
    }

    @Override
    protected void handleDetailsBack(int detailFlag) {
        if (detailFlag == Constants.NUMBER_NINE) {
            openDetailsView(null);
        }
    }

    public static ArtistFragment newInstance() {
        return new ArtistFragment();
    }
}
