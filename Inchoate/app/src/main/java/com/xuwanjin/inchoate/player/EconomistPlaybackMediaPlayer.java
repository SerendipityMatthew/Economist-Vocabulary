package com.xuwanjin.inchoate.player;

import android.media.MediaPlayer;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.xuwanjin.inchoate.events.AudioPlayerEvent;
import com.xuwanjin.inchoate.model.Article;

import java.util.ArrayList;
import java.util.List;

public class EconomistPlaybackMediaPlayer {
    public static final String TAG = "EconomistPlayer";
    private static volatile EconomistPlaybackMediaPlayer sInstance;
    MediaPlayer mMediaPlayer;
    // 播放列表, 在 today news 界面, 播放列表里只有一个,
    // 在 weekly 界面的, 播放列表里是所有的文章
    // issue date, section name , article title 确定唯一的一篇文章
    private List<Article> mPlayList = new ArrayList<>();
    private Article mCurrentArticle;
    private boolean isPaused;

    public EconomistPlaybackMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    public static EconomistPlaybackMediaPlayer getInstance() {
        if (sInstance == null) {
            synchronized (EconomistPlaybackMediaPlayer.class) {
                if (sInstance == null) {
                    sInstance = new EconomistPlaybackMediaPlayer();
                }
            }
        }
        return sInstance;
    }


    public void play(AudioPlayerEvent playerEvent) {
        mMediaPlayer.start();
        isPaused = false;

    }

}
