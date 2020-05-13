package com.xuwanjin.inchoate.ui.playing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.xuwanjin.inchoate.InchoateActivity;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.events.SlidingUpControllerEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.player.EconomistService;
import com.xuwanjin.inchoate.player.IPlayer;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;
import com.xuwanjin.inchoate.timber_style.IEconomistService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AudioPlayerFragment extends Fragment implements IPlayer.Callback {
    private IPlayer mPlayService;
    private IEconomistService mEconomistService;
    private Context mContext;
    private List<Article> mArticleList;
    private boolean isPlayWholeIssue = false;
    private ImageView playToggle;
    private AppCompatSeekBar seekBarProgress;
    private Handler mHandler = new Handler();
    public final int DELAY_TIME = 1000;
    private TextView audioPlayed;
    private TextView audioLeft;
    private ImageView replay;
    private ImageView forward;
    private ImageView issueCategoryMenu;
    private ImageView next;
    private ImageView articleCoverImage;
    private ImageView last;
    private TextView playFlyTitle;
    private TextView audioPlayTitle;
    private TextView playSpeed;
    private View view;
    private Article mAudioPlayingArticle;
    private RecyclerView audioIssueCategoryRV;
    private ImageView barPlay;
    private AppCompatSeekBar barPlayingProgress;
    private ImageView barSkip;
    private ImageView barPlayingClose;
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) {
                return;
            }
            if (EconomistPlayerTimberStyle.isPlaying()) {
                int progress = (int) (seekBarProgress.getMax() * (float) EconomistPlayerTimberStyle.getCurrentPosition() /
                        (float) getCurrentArticleDuration());
                updateProgressText(EconomistPlayerTimberStyle.getCurrentPosition());
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
            mEconomistService = IEconomistService.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void updateProgressText(int progress) {
        audioLeft.setText(Utils.getDurationFormat(mAudioPlayingArticle.audioDuration - progress));
        audioPlayed.setText(Utils.getDurationFormat(progress));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();

        view = inflater.inflate(R.layout.fragment_audio_play, container, false);
        mArticleList = InchoateApplication.getAudioPlayingArticleListCache();
        if (mArticleList == null || mArticleList.size() == 0) {
            return null;
        }
        mAudioPlayingArticle = InchoateApplication.getDisplayArticleCache();
        initView();
        initData();
        initOnListener();

        return view;
    }

    public void initView() {
        barPlay = view.findViewById(R.id.bar_play);
        barPlayingProgress = view.findViewById(R.id.bar_playing_progress);
        barSkip = view.findViewById(R.id.bar_skip_15);
        barPlayingClose = view.findViewById(R.id.bar_playing_close);

        last = view.findViewById(R.id.last);
        next = view.findViewById(R.id.next);

        articleCoverImage = view.findViewById(R.id.article_cover_image);
        playFlyTitle = view.findViewById(R.id.audio_play_fly_title);
        audioPlayTitle = view.findViewById(R.id.audio_play_title);
        issueCategoryMenu = view.findViewById(R.id.issue_category_menu);
        audioPlayed = view.findViewById(R.id.audio_played);
        audioLeft = view.findViewById(R.id.audio_left);
        replay = view.findViewById(R.id.replay);
        forward = view.findViewById(R.id.forward);
        playToggle = view.findViewById(R.id.play_toggle);
        seekBarProgress = view.findViewById(R.id.playing_progress);
        playSpeed = view.findViewById(R.id.play_speed);
        audioIssueCategoryRV = view.findViewById(R.id.audio_issue_category_rv);
    }

    private void initData() {
        if (mAudioPlayingArticle == null) {
            return;
        }
        Glide.with(getContext()).load(mAudioPlayingArticle.imageUrl).into(articleCoverImage);
        playFlyTitle.setText(mAudioPlayingArticle.flyTitle);
        audioPlayTitle.setText(mAudioPlayingArticle.title);
        audioPlayed.setText(Utils.getDurationFormat(0));
        audioLeft.setText(Utils.getDurationFormat(mAudioPlayingArticle.audioDuration));
        seekBarProgress.setMax((int) mAudioPlayingArticle.audioDuration);
        playToggle.setImageResource(R.mipmap.pause);
    }

    public void initOnListener() {
        barPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                panelState.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                EventBus.getDefault().post(panelState);
            }
        });

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
                EconomistPlayerTimberStyle.seekToPosition(seekBar.getProgress());
                try {
                    if (mEconomistService.isPlaying()) {
                        mHandler.removeCallbacks(mProgressCallback);
                        mHandler.post(mProgressCallback);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        playToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayService != null && mPlayService.isPlaying()) {
                    mPlayService.pause();
                    playToggle.setImageResource(R.mipmap.play);
                } else {
                    mPlayService.play(mAudioPlayingArticle, isPlayWholeIssue);
                    playToggle.setImageResource(R.mipmap.pause);
                }
            }
        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void updatePlayToggle(boolean isPlaying) {
        playToggle.setImageResource(isPlaying ? R.mipmap.pause : R.mipmap.play);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayService != null && mPlayService.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        if (isPlaying) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
    }
}
