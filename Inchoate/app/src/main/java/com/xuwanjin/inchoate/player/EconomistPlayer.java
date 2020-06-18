package com.xuwanjin.inchoate.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.xuwanjin.inchoate.model.Article;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class EconomistPlayer implements IPlayer, MediaPlayer.OnCompletionListener {
    public static final String TAG = "EconomistPlayer";
    private static final WeakHashMap<Context, ServiceBinder> SERVICE_WEAK_HASHMAP = new WeakHashMap<>();
    private static volatile EconomistPlayer sInstance;
    MediaPlayer mMediaPlayer;
    private List<Callback> mCallbacks = new ArrayList<>(2);
    /*
        播放列表, 在 today news 界面, 播放列表里只有一个,
        在 weekly 界面的, 播放列表里是所有的文章
        issue date, section name , article title 确定唯一的一篇文章
     */
    private List<Article> mPlayList = new ArrayList<>();
    private Article mCurrentArticle;
    private boolean isPaused;

    public EconomistPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
    }

    public static EconomistPlayer getInstance() {
        if (sInstance == null) {
            synchronized (EconomistPlayer.class) {
                if (sInstance == null) {
                    sInstance = new EconomistPlayer();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void setPlayList(List<Article> articleList) {
        this.mPlayList = articleList;
    }

    @Override
    public boolean play() {
        if (isPaused) {
            mMediaPlayer.start();
            notifyPlayStatusChanged(true);
        }
        isPaused = false;
        return false;
    }

    public void setCurrentArticle(Article article) {
        this.mCurrentArticle = article;
    }

    public Article getCurrentArticle() {
        return mCurrentArticle;
    }

    private void notifyPlayStatusChanged(boolean isPlaying) {
        for (Callback callback : mCallbacks) {
            callback.onPlayStatusChanged(isPlaying);
        }
    }

    @Override
    public boolean play(Article article, boolean isPlayWholeIssue) {
        setCurrentArticle(article);
        if (isPaused) {
            mMediaPlayer.start();
            notifyPlayStatusChanged(true);
        }
        if (isPlayWholeIssue) {
            List<Article> articleList = new ArrayList<>();
            articleList.add(article);
            setPlayList(articleList);
        }

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(article.audioUrl);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            notifyPlayStatusChanged(true);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean play(List<Article> articleList, int startIndex) {
        return false;
    }

    @Override
    public boolean playNext() {
        return false;
    }

    @Override
    public boolean playLast() {
        return false;
    }

    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public Article getPlayingSong() {
        return null;
    }

    @Override
    public boolean seekTo(int progress) {
        Log.d(TAG, "seekTo: progress = " + progress);
        Article currentArticle = getCurrentArticle();
        if (currentArticle != null) {
            if (progress >= 0 && progress < getCurrentArticle().audioDuration) {
                mMediaPlayer.seekTo(progress);
            }
            if (progress >= getCurrentArticle().audioDuration) {
                onCompletion(mMediaPlayer);
            }
            return true;
        }
        return false;
    }

    @Override
    public void registerCallback(Callback callback) {

    }

    @Override
    public void unregisterCallback(Callback callback) {

    }

    @Override
    public void removeCallbacks() {

    }

    @Override
    public void releasePlayer() {
        mPlayList = null;
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        sInstance = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public static final class ServiceBinder implements ServiceConnection {
        private ServiceConnection mCallBack;
        private Context mContext;

        public ServiceBinder(final ServiceConnection callback, final Context context) {
            mCallBack = callback;
            mContext = context;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onBindingDied(ComponentName name) {

        }

        @Override
        public void onNullBinding(ComponentName name) {

        }
    }
}
