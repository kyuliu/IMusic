package com.example.imusic.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.imusic.fragment.AboutFragment;
import com.example.imusic.fragment.AlbumFragment;
import com.example.imusic.fragment.ArtistFragment;
import com.example.imusic.fragment.PlayListFragment;
import com.example.imusic.fragment.SongCategoryFragment;
import com.example.imusic.fragment.SongFragment;

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class SongViewPagerAdapter
        extends FragmentStateAdapter {


    public SongViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return SongCategoryFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
