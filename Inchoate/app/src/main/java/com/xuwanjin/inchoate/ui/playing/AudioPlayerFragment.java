package com.xuwanjin.inchoate.ui.playing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.player.EconomistService;
import com.xuwanjin.inchoate.player.IPlayer;

public class AudioPlayerFragment extends Fragment implements IPlayer.Callback{
    private IPlayer mPlayService;
    private Context mContext;
    private Article mArticle;
    private boolean isPlayWholeIssue = false;
    private ImageView playToggle;
    AppCompatSeekBar seekBarProgress;
    private Handler mHandler = new Handler();
    public final int DELAY_TIME = 1000;
    TextView audioPlayed;
    TextView audioLeft;
    ImageView replay;
    ImageView forward;
    ImageView issueCategoryMenu;
    ImageView next;
    ImageView articleCoverImage;
    ImageView last;
    TextView playFlyTitle;
    TextView audioPlayTitle;
    TextView playSpeed;
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) {
                return;
            }
            if (mPlayService.isPlaying()) {
                int progress = (int) (seekBarProgress.getMax() * (float) mPlayService.getProgress() /
                        (float) getCurrentArticleDuration());
                updateProgressText(mPlayService.getProgress());
                if (progress >= 0 && progress <= seekBarProgress.getMax()) {
                    seekBarProgress.setProgress(progress);
                }
                mHandler.postDelayed(this, DELAY_TIME);
            }
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = ((EconomistService.LocalBinder) service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void updateProgressText(int progress) {
        audioLeft.setText(Utils.getDurationFormat(mArticle.audioDuration - progress));
        audioPlayed.setText(Utils.getDurationFormat(progress));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContext.bindService(
                new Intent(getContext(), EconomistService.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mArticle = InchoateApplication.getDisplayArticleCache();
        View view = inflater.inflate(R.layout.fragment_audio_play, container, false);
        articleCoverImage = view.findViewById(R.id.article_cover_image);
        Glide.with(getContext()).load(mArticle.mainArticleImage).into(articleCoverImage);
        playFlyTitle = view.findViewById(R.id.audio_play_fly_title);
        audioPlayTitle = view.findViewById(R.id.audio_play_title);
        playFlyTitle.setText(mArticle.flyTitle);
        audioPlayTitle.setText(mArticle.title);
        last = view.findViewById(R.id.last);
        next = view.findViewById(R.id.next);
        issueCategoryMenu = view.findViewById(R.id.issue_category_menu);
        audioPlayed = view.findViewById(R.id.audio_played);
        audioLeft = view.findViewById(R.id.audio_left);
        replay = view.findViewById(R.id.replay);
        forward = view.findViewById(R.id.forward);
        playToggle = view.findViewById(R.id.play_toggle);
        playToggle.setImageResource(R.mipmap.pause);
        audioPlayed.setText(Utils.getDurationFormat(0));
        audioLeft.setText(Utils.getDurationFormat(mArticle.audioDuration));
        seekBarProgress = view.findViewById(R.id.playing_progress);
        seekBarProgress.setMax((int) mArticle.audioDuration);
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //fromUser 表示进度条被用户拖动着
                Log.d("Matthew", "onProgressChanged: fromUser = " + fromUser);
                if (fromUser) {
                    updateProgressText(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("Matthew", "onStopTrackingTouch: " + seekBar.getProgress());
                mPlayService.seekTo(seekBar.getProgress());
                if (mPlayService.isPlaying()){
                    mHandler.removeCallbacks(mProgressCallback);
                    mHandler.post(mProgressCallback);
                }
            }
        });
        playSpeed = view.findViewById(R.id.play_speed);
        playToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayService != null && mPlayService.isPlaying()) {
                    mPlayService.pause();
                    playToggle.setImageResource(R.mipmap.play);
                } else {
                    mPlayService.play(mArticle, isPlayWholeIssue);
                    playToggle.setImageResource(R.mipmap.pause);
                }
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void updatePlayToggle(boolean isPlaying){
        playToggle.setImageResource(isPlaying?R.mipmap.pause:R.mipmap.play);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mPlayService != null && mPlayService.isPlaying()){
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mProgressCallback);
        mContext.unbindService(mConnection);
    }

    private int getCurrentArticleDuration() {
        Article article = mPlayService.getPlayingSong();
        int duration = 0;
        if (article != null) {
            duration = (int) article.audioDuration;
        }
        return duration;
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
        updatePlayToggle(isPlaying);
        if (isPlaying){
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }else {
            mHandler.removeCallbacks(mProgressCallback);
        }
    }
}
