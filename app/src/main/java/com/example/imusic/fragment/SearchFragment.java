package com.example.imusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.imusic.MusicApplication;
import com.example.imusic.R;
import com.example.imusic.activity.PlayListActivity;
import com.example.imusic.adapter.DetailsViewAdapter;
import com.example.imusic.base.BaseFragment;
import com.example.imusic.base.BaseObserver;
import com.example.imusic.base.factory.RecyclerFactory;
import com.example.imusic.base.listener.OnFlowLayoutClickListener;
import com.example.imusic.fragment.dialogfrag.MoreMenuBottomDialog;
import com.example.imusic.model.MoreMenuStatus;
import com.example.imusic.model.MusicBean;
import com.example.imusic.model.SearchCategoryBean;
import com.example.imusic.model.SearchHistoryBean;
import com.example.imusic.model.greendao.SearchHistoryBeanDao;
import com.example.imusic.util.Constants;
import com.example.imusic.util.LogUtil;
import com.example.imusic.util.MusicDaoUtil;
import com.example.imusic.util.SnakbarUtil;
import com.example.imusic.view.FlowLayoutView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: Lsp
 * @ Name:    SearchFragment
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/3/16/ 16:20
 * @ Des:     TODO
 */
public class SearchFragment extends BaseFragment {
    @BindView(R.id.tv_no_search_result)
    TextView mTvNoSearchResult;
    @BindView(R.id.iv_search_clear)
    ImageView mIvSearchClear;
    @BindView(R.id.sticky_view_search)
    TextView mtvStickyView;
    @BindView(R.id.ll_list_view)
    LinearLayout mLinearDetail;
    @BindView(R.id.ll_history)
    LinearLayout mLayoutHistory;
    @BindView(R.id.flowlayout)
    FlowLayoutView mFlowLayoutView;
    private DetailsViewAdapter mSearchDetailAdapter;
    private Disposable mDisposableSearch;
    private Disposable mDisposableMoreMenu;
    private boolean mIsFirst = false;
    private int mPosition;
    private SearchHistoryBeanDao mSearchDao;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        rxbusData();
        LogUtil.d(TAG,"=========== searchPostion    " + mPosition);

    }

    private void rxbusData() {
        if (mDisposableSearch == null) {
            mDisposableSearch = mBus.toObserverable(SearchCategoryBean.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(SearchFragment.this::searchSong);
        }
        if (mDisposableMoreMenu == null) {
            mDisposableMoreMenu = mBus.toObserverable(MoreMenuStatus.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::moreMenu);
        }
    }

    private void initListener() {
        mSearchDetailAdapter.setOnItemMenuListener((position, musicBean) -> MoreMenuBottomDialog.newInstance(musicBean, position, false, false).getBottomDialog(mActivity));
        mIvSearchClear.setOnClickListener(v -> {
            mSearchDao.deleteAll();
            mFlowLayoutView.removeAllViews();
            mLayoutHistory.setVisibility(View.INVISIBLE);
            mLinearDetail.setVisibility(View.INVISIBLE);
        });
        mFlowLayoutView.setItemClickListener((position, bean) -> initSearch(bean.getSearchContent()));
    }

    /**
     * 点击搜索记录
     *
     * @param searchContent 搜索内容
     */
    private void initSearch(String searchContent) {
        // 填入输入框
        if (mContext instanceof OnFlowLayoutClickListener) {
            ((OnFlowLayoutClickListener) mContext).click(searchContent);
        }
        searchMusic(searchContent);
    }


    private void clearAdapter() {
        if (mSearchDetailAdapter != null) {
            mSearchDetailAdapter.clear();
        }
    }

    /**
     * 搜索音乐
     *
     * @param searchconditions 搜索关键字
     */
    private void searchMusic(String searchconditions) {
        MusicDaoUtil.getSearchResult(mMusicBeanDao, searchconditions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MusicBean>>() {
                    @Override
                    public void onNext(List<MusicBean> musicBeanList) {

                        mSearchDetailAdapter.setNewData(musicBeanList);
                        mLayoutHistory.setVisibility(View.GONE);
                        mTvNoSearchResult.setVisibility(View.GONE);
                        mLinearDetail.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mIsFirst) {
                            mLayoutHistory.setVisibility(View.GONE);
                            mLinearDetail.setVisibility(View.GONE);
                            mTvNoSearchResult.setVisibility(View.VISIBLE);
                        }
                    }

                });
    }

    private void historyViewVisibility() {
        List<SearchHistoryBean> historyList = mSearchDao.queryBuilder().list();
        if (historyList != null && historyList.size() > 0) {
            mLayoutHistory.setVisibility(View.VISIBLE);
            mTvNoSearchResult.setVisibility(View.GONE);
            Collections.sort(historyList);
            mFlowLayoutView.clearView(historyList);
        } else {
            mLayoutHistory.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mSearchDao = MusicApplication.getIntstance().getSearchDao();
        // 搜索记录
        List<SearchHistoryBean> searchList = mSearchDao.queryBuilder().list();
        if (searchList != null && searchList.size() > 0) {
            mLayoutHistory.setVisibility(View.VISIBLE);
            Collections.sort(searchList);
            mFlowLayoutView.setData(searchList);
        }
        // 列表数据
        mSearchDetailAdapter = new DetailsViewAdapter(mActivity, null, Constants.NUMBER_THREE);
        RecyclerView recyclerView = RecyclerFactory.createRecyclerView(1, mSearchDetailAdapter);
        mLinearDetail.addView(recyclerView);
        if (getArguments() != null) {
            mPosition = getArguments().getInt("position");
            String searchArtist = getArguments().getString("searchArtist");
            setFlagAndSearch(searchArtist, 1);
        }
    }

    private void searchSong(SearchCategoryBean categoryBean) {
        int categoryFlag = categoryBean.getCategoryFlag();
        String condition = categoryBean.getSearchCondition();
        if (condition != null) {
            mIsFirst = true;
        }
        switch (categoryFlag) {
            case 0:
                setFlagAndSearch(condition, Constants.NUMBER_THREE);
                break;
            case 1:
                setFlagAndSearch(condition, Constants.NUMBER_THREE);
                break;
            case 2:
                setFlagAndSearch(condition, Constants.NUMBER_TWO);
                break;
            case 3:
                setFlagAndSearch(condition, Constants.NUMBER_ONE);
                break;
            case 4:
                setFlagAndSearch(condition, Constants.NUMBER_FOUR);
                break;
            case 9:
                clearAdapter();
                mLinearDetail.setVisibility(View.INVISIBLE);
                historyViewVisibility();
                break;
            case 10:
                historyViewVisibility();
                clearAdapter();
                mLinearDetail.setVisibility(View.INVISIBLE);
                mTvNoSearchResult.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }

    private void setFlagAndSearch(String condition, int numberTwo) {
        mSearchDetailAdapter.setDataFlag(numberTwo);
        searchMusic(condition);
    }

    private void moreMenu(MoreMenuStatus moreMenuStatus) {
        MusicBean musicBean = moreMenuStatus.getMusicBean();
        switch (moreMenuStatus.getPosition()) {
            case Constants.NUMBER_ZERO:
                Intent intent = new Intent(mContext, PlayListActivity.class);
                intent.putExtra(Constants.SONG_NAME, musicBean.getTitle());
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.dialog_push_in, 0);
                break;
            case Constants.NUMBER_ONE:
                SnakbarUtil.keepGoing(mLayoutHistory);
                break;
            case Constants.NUMBER_TWO:
                break;
            case Constants.NUMBER_THREE:
                SnakbarUtil.keepGoing(mtvStickyView);
                break;
            case Constants.NUMBER_FOUR:
                // 刷新搜索列表, 后续完成
                mMusicBeanDao.delete(moreMenuStatus.getMusicBean());
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDisposableSearch != null) {
            mDisposableSearch.dispose();
            mDisposableSearch = null;
        }
        if (mDisposableMoreMenu != null) {
            mDisposableMoreMenu.dispose();
            mDisposableMoreMenu = null;
        }
    }

    public static SearchFragment newInstance(int position, String searchArtist) {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        args.putInt("position", position);
        args.putString("searchArtist", searchArtist);
        fragment.setArguments(args);
        return fragment;
    }
}
