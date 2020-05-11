package com.xuwanjin.inchoate.player;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.xuwanjin.inchoate.model.Article;

import java.util.List;

public class ExoPlayerWrapper implements IPlayer {
    public SimpleExoPlayer mSimpleExoPlayer;
    private Context mContext;
    private MediaSource mMediaSource;

    public ExoPlayerWrapper(Context context) {
        this.mContext = context;
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultRenderersFactory(context)
                , new DefaultTrackSelector(), new DefaultLoadControl.Builder().createDefaultLoadControl());
        mSimpleExoPlayer.setSeekParameters(SeekParameters.EXACT);
    }

    @Override
    public void setPlayList(List<Article> articleList) {

    }

    public void setDataSource(String audioPath) {
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(
                "Matthew", null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true
        );
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, null, defaultHttpDataSourceFactory);
        DefaultExtractorsFactory defaultExtractorsFactory = new DefaultExtractorsFactory();
        defaultExtractorsFactory.setConstantBitrateSeekingEnabled(true);
        ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(dataSourceFactory, defaultExtractorsFactory);
        mMediaSource = factory.createMediaSource(Uri.parse(audioPath));
    }

    public void prepare(){
        mSimpleExoPlayer.prepare(mMediaSource);
    }

    public void start() {
        mSimpleExoPlayer.setPlayWhenReady(true);
        mSimpleExoPlayer.setPlaybackParameters(new PlaybackParameters(1.0f));
    }

    @Override
    public boolean play() {
        return false;
    }

    @Override
    public boolean play(Article article, boolean isPlayWholeIssue) {
        return false;
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

    }
}
