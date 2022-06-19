package com.example.imusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.imusic.util.Constants;
import com.example.imusic.util.RxBus;
import com.example.imusic.util.StringUtil;

/**
 * @ Name:   CountdownService
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2019/1/2/ 22:49
 * @ Des:    倒计时服务
 * @author Luoshipeng
 */
public class CountdownService extends Service {

    private MusicTimer mMusicTimer;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long countdownTime = intent.getLongExtra(Constants.COUNTDOWN_TIME, 0);
        mMusicTimer = new MusicTimer(countdownTime, 1000);
        mMusicTimer.start();
        return START_NOT_STICKY;
    }


    private class MusicTimer extends CountDownTimer {
        private MusicTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String countdownTime = StringUtil.parseDuration((int) millisUntilFinished);
            RxBus.getInstance().post(Constants.COUNTDOWN_TIME,countdownTime);
        }

        @Override
        public void onFinish() {
            cancel();
            sendCloseMusicBroadcast();
            stopSelf();
        }

        private void sendCloseMusicBroadcast() {
            Intent intent = new Intent(Constants.ACTION_MUSIC);
            intent.putExtra(Constants.NOTIFY_BUTTON_ID, Constants.COUNTDOWN_FINISH);
            sendBroadcast(intent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMusicTimer != null) {
            mMusicTimer.cancel();
            mMusicTimer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
