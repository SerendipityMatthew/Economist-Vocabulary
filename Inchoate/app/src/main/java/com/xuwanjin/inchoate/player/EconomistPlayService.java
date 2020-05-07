package com.xuwanjin.inchoate.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.xuwanjin.inchoate.model.Article;

import java.util.List;

public class EconomistService extends MediaBrowserServiceCompat implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        IPlayer, IPlayer.Callback {
    public static final String TAG = "EconomistService";
    private EconomistPlayer mPlayer;
    private final Binder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public EconomistService getService() {
            return EconomistService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = EconomistPlayer.getInstance();
        mPlayer.registerCallback(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }


    public void stop() {
        mPlayer.releasePlayer();

    }

    @Override
    public void setPlayList(List<Article> articleList) {

    }

    @Override
    public boolean play() {
        return mPlayer.play();
    }

    @Override
    public boolean play(Article article, boolean isPlayWholeIssue) {
        return mPlayer.play(article, isPlayWholeIssue);
    }

    @Override
    public boolean play(List<Article> articleList, int startIndex) {
        return mPlayer.play(articleList, startIndex);
    }

    @Override
    public boolean playNext() {
        return false;
    }

    @Override
    public boolean playLast() {
        return false;
    }

    public boolean pause() {
        return mPlayer.pause();
    }


    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }


    @Override
    public int getProgress() {
        return mPlayer.getProgress();
    }

    @Override
    public Article getPlayingSong() {
        return mPlayer.getPlayingSong();
    }

    @Override
    public boolean seekTo(int progress) {
        Log.d(TAG, "seekTo: progress");
        return mPlayer.seekTo(progress);
    }

    @Override
    public void registerCallback(Callback callback) {
        mPlayer.registerCallback(callback);

    }

    @Override
    public void unregisterCallback(Callback callback) {
        mPlayer.unregisterCallback(callback);
    }

    @Override
    public void removeCallbacks() {
        mPlayer.removeCallbacks();
    }

    @Override
    public void releasePlayer() {
        mPlayer.releasePlayer();
    }

    public void release() {
        if (mPlayer != null) {
            mPlayer.releasePlayer();
        }
    }

    public void next() {

    }

    public void skipToNext() {

    }

    public void skipToLast() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
//        switch (MediaPlayer.MEDIA_ERROR_SERVER_DIED):
        return false;
    }

    @Override
    public void onSwitchLast(@Nullable Article last) {

    }

    @Override
    public void onSwitchNext(@Nullable Article next) {

    }

    @Override
    public void onComplete(@Nullable Article next) {

    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {

    }
}
