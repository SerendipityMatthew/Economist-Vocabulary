package com.xuwanjin.inchoate.player;

import android.content.Context;

import com.xuwanjin.inchoate.events.AudioPlayerEvent;

public class LocalServiceMediaPlayer extends EconomistPlaybackMediaPlayer {
    public ExoPlayerWrapper mMediaPlayer;
    private static LocalServiceMediaPlayer sLocalServiceMediaPlayer;
    private Context mContext;
    public LocalServiceMediaPlayer(Context context){
        this.mContext = context;
        mMediaPlayer = new ExoPlayerWrapper(context);
    }
    public static LocalServiceMediaPlayer getInstance(Context context){
        if (sLocalServiceMediaPlayer == null){
            sLocalServiceMediaPlayer = new LocalServiceMediaPlayer(context);
        }
        return sLocalServiceMediaPlayer;
    }

    public void setDataSource(String audioPath){
        mMediaPlayer.setDataSource(audioPath);
    }
    public void prepare(){
        mMediaPlayer.prepare();
    }
    public void start(){
        mMediaPlayer.start();
    }
    public void play(AudioPlayerEvent playerEvent){
        String audioPath = playerEvent.article.audioUrl;
        setDataSource(audioPath);
        prepare();
        start();
    }
}
