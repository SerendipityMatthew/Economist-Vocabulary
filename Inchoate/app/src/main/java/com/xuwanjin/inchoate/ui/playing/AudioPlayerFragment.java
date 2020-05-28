package com.xuwanjin.inchoate.ui.playing;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.events.PlayEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.player.IPlayer;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;
import com.xuwanjin.inchoate.timber_style.IEconomistService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.xuwanjin.inchoate.Constants.FORWARD_BY_SECONDS_PREFERENCE;
import static com.xuwanjin.inchoate.Constants.INCHOATE_PREFERENCE_FILE_NAME;
import static com.xuwanjin.inchoate.Constants.REWIND_BY_SECONDS_PREFERENCE;
import static com.xuwanjin.inchoate.Constants.REWIND_OR_FORWARD_PREFERENCE;

public class AudioPlayerFragment extends Fragment implements IPlayer.Callback {
    public static final String TAG = "AudioPlayerFragment";
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
    private View.OnClickListener playOrPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EconomistPlayerTimberStyle.playOrPause();
        }
    };
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) {
                return;
            }

            int progress = EconomistPlayerTimberStyle.getCurrentPosition();
            updateProgressText(progress);
            if (progress >= 0 && progress <= seekBarProgress.getMax()) {
                seekBarProgress.setProgress(progress);
                barPlayingProgress.setProgress(progress);
            }

            mHandler.postDelayed(mProgressCallback, DELAY_TIME);
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mEconomistService = IEconomistService.Stub.asInterface(service);
            Log.d(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    private AppCompatSeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new AppCompatSeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //fromUser 表示进度条被用户拖动着
            if (fromUser) {
                updateProgressText(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            EconomistPlayerTimberStyle.seekToPosition(seekBar.getProgress());
        }
    };

    public void updateProgressText(int progress) {
        if (mAudioPlayingArticle != null) {
            audioLeft.setText(Utils.getDurationFormat(mAudioPlayingArticle.audioDuration - progress / 1000));
        } else {
            audioLeft.setText(Utils.getDurationFormat(0));
        }
        audioPlayed.setText(Utils.getDurationFormat(progress / 1000));
    }

    public View getAudioPlayingBar() {
        if (getView() == null) {
            return null;
        }
        return getView().findViewById(R.id.audio_playing_bar);
    }

    public ImageView getPlayButton() {
        View view = getAudioPlayingBar();
        int visibility = view.getVisibility();
        if (visibility == View.GONE) {
            return playToggle;
        } else if (visibility == View.INVISIBLE) {
            return barPlay;
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_audio_play, container, false);
        mArticleList = InchoateApp.getAudioPlayingArticleListCache();
        if (mArticleList == null || mArticleList.size() == 0) {
            return null;
        }
        mAudioPlayingArticle = InchoateApp.getDisplayArticleCache();
        initView();
        initData();
        initOnListener();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.removeCallbacks(mProgressCallback);
        mHandler.post(mProgressCallback);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayNextUpdateUI(PlayEvent playEvent) {
        if (playEvent.isPlaySkip) {
            mAudioPlayingArticle = playEvent.mArticle;
            initData();
        }
    }

    private void initData() {
        if (mAudioPlayingArticle == null) {
            return;
        }
        Glide.with(getContext())
                .load(mAudioPlayingArticle.mainArticleImage)
                .into(articleCoverImage);
        playFlyTitle.setText(mAudioPlayingArticle.flyTitle);
        audioPlayTitle.setText(mAudioPlayingArticle.title);
        audioPlayed.setText(Utils.getDurationFormat(0));
        audioLeft.setText(Utils.getDurationFormat(mAudioPlayingArticle.audioDuration));
        seekBarProgress.setMax((int) mAudioPlayingArticle.audioDuration * 1000);
        barPlayingProgress.setMax((int) mAudioPlayingArticle.audioDuration * 1000);
        playToggle.setImageResource(R.mipmap.pause);
        barPlay.setImageResource(R.mipmap.pause);
    }

    public void initOnListener() {
        barPlay.setOnClickListener(playOrPauseListener);

        seekBarProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
        barPlayingProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
        playToggle.setOnClickListener(playOrPauseListener);

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                        getActivity().getSharedPreferences(INCHOATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
                preferences.edit().putString(REWIND_OR_FORWARD_PREFERENCE, REWIND_BY_SECONDS_PREFERENCE).apply();
                EconomistPlayerTimberStyle.seekToIncrementPosition();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                        getActivity().getSharedPreferences(INCHOATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
                preferences.edit().putString(REWIND_OR_FORWARD_PREFERENCE, FORWARD_BY_SECONDS_PREFERENCE).apply();
                EconomistPlayerTimberStyle.seekToIncrementPosition();
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EconomistPlayerTimberStyle.playPrevious();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EconomistPlayerTimberStyle.playNext();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayEvent(PlayEvent playEvent) {
        updatePlayButton(playEvent.isPlaying);
    }

    public void updatePlayButton(boolean isPlaying) {
        playToggle.setImageResource(isPlaying ? R.mipmap.pause : R.mipmap.play);
        barPlay.setImageResource(isPlaying ? R.mipmap.pause : R.mipmap.play);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mProgressCallback);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private int getCurrentArticleDuration() {
        Article article = mAudioPlayingArticle;
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
//        updatePlayToggle(isPlaying);
        if (isPlaying) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
    }

}
