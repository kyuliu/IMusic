package com.example.imusic.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.imusic.base.BasePagerAdapter;
import com.example.imusic.fragment.LyricsFragment;
import com.example.imusic.model.qq.SearchLyricsBean;

import java.util.List;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class SearchLyricsPagerAdapter
        extends FragmentStateAdapter {
    private List<SearchLyricsBean> mLyricsList;

    public SearchLyricsPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<SearchLyricsBean> lyricsList) {
        super(fragmentActivity);
        mLyricsList = lyricsList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return LyricsFragment.newInstance(position, mLyricsList.get(position).getLyrics());
    }

    @Override
    public int getItemCount() {
        return mLyricsList != null ? mLyricsList.size() : 0;
    }

}
