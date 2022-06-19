package com.example.imusic.model.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.example.imusic.model.AlbumInfo;
import com.example.imusic.model.MusicBean;
import com.example.imusic.model.MusicInfo;
import com.example.imusic.model.PlayListBean;
import com.example.imusic.model.SearchHistoryBean;

import com.example.imusic.model.greendao.AlbumInfoDao;
import com.example.imusic.model.greendao.MusicBeanDao;
import com.example.imusic.model.greendao.MusicInfoDao;
import com.example.imusic.model.greendao.PlayListBeanDao;
import com.example.imusic.model.greendao.SearchHistoryBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig albumInfoDaoConfig;
    private final DaoConfig musicBeanDaoConfig;
    private final DaoConfig musicInfoDaoConfig;
    private final DaoConfig playListBeanDaoConfig;
    private final DaoConfig searchHistoryBeanDaoConfig;

    private final AlbumInfoDao albumInfoDao;
    private final MusicBeanDao musicBeanDao;
    private final MusicInfoDao musicInfoDao;
    private final PlayListBeanDao playListBeanDao;
    private final SearchHistoryBeanDao searchHistoryBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        albumInfoDaoConfig = daoConfigMap.get(AlbumInfoDao.class).clone();
        albumInfoDaoConfig.initIdentityScope(type);

        musicBeanDaoConfig = daoConfigMap.get(MusicBeanDao.class).clone();
        musicBeanDaoConfig.initIdentityScope(type);

        musicInfoDaoConfig = daoConfigMap.get(MusicInfoDao.class).clone();
        musicInfoDaoConfig.initIdentityScope(type);

        playListBeanDaoConfig = daoConfigMap.get(PlayListBeanDao.class).clone();
        playListBeanDaoConfig.initIdentityScope(type);

        searchHistoryBeanDaoConfig = daoConfigMap.get(SearchHistoryBeanDao.class).clone();
        searchHistoryBeanDaoConfig.initIdentityScope(type);

        albumInfoDao = new AlbumInfoDao(albumInfoDaoConfig, this);
        musicBeanDao = new MusicBeanDao(musicBeanDaoConfig, this);
        musicInfoDao = new MusicInfoDao(musicInfoDaoConfig, this);
        playListBeanDao = new PlayListBeanDao(playListBeanDaoConfig, this);
        searchHistoryBeanDao = new SearchHistoryBeanDao(searchHistoryBeanDaoConfig, this);

        registerDao(AlbumInfo.class, albumInfoDao);
        registerDao(MusicBean.class, musicBeanDao);
        registerDao(MusicInfo.class, musicInfoDao);
        registerDao(PlayListBean.class, playListBeanDao);
        registerDao(SearchHistoryBean.class, searchHistoryBeanDao);
    }
    
    public void clear() {
        albumInfoDaoConfig.clearIdentityScope();
        musicBeanDaoConfig.clearIdentityScope();
        musicInfoDaoConfig.clearIdentityScope();
        playListBeanDaoConfig.clearIdentityScope();
        searchHistoryBeanDaoConfig.clearIdentityScope();
    }

    public AlbumInfoDao getAlbumInfoDao() {
        return albumInfoDao;
    }

    public MusicBeanDao getMusicBeanDao() {
        return musicBeanDao;
    }

    public MusicInfoDao getMusicInfoDao() {
        return musicInfoDao;
    }

    public PlayListBeanDao getPlayListBeanDao() {
        return playListBeanDao;
    }

    public SearchHistoryBeanDao getSearchHistoryBeanDao() {
        return searchHistoryBeanDao;
    }

}
