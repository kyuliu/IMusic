package com.example.imusic.base.listener;

import com.example.imusic.model.qq.AlbumSong;
import com.example.imusic.model.qq.SongLrc;

import java.util.List;

/**
 * @author luoshipeng
 * createDate：2019/12/10 0010 14:31
 * className   OnAlbumDetailListener
 * Des：TODO
 */
public interface OnSearchLyricsListener {
    void searchResult(List<SongLrc> songLrcList);
}
