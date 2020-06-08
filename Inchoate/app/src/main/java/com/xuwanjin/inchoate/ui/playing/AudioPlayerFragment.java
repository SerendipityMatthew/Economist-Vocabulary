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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.events.PlayEvent;
import com.xuwanjin.inchoate.events.SlidingUpControllerEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.player.IPlayer;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;
import com.xuwanjin.inchoate.timber_style.IEconomistService;
import com.xuwanjin.inchoate.ui.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.xuwanjin.inchoate.Constants.FORWARD_BY_SECONDS_PREFERENCE;
import static com.xuwanjin.inchoate.Constants.INCHOATE_PREFERENCE_FILE_NAME;
import static com.xuwanjin.inchoate.Constants.REWIND_BY_SECONDS_PREFERENCE;
import static com.xuwanjin.inchoate.Constants.REWIND_OR_FORWARD_PREFERENCE;

public class AudioPlayerFragment extends BaseFragment implements IPlayer.Callback {
    public static final String TAG = "AudioPlayerFragment";
    private IPlayer mPlayService;
    private IEconomistService mEconomistService;
    private Context mContext;
    private List<Article> mArticleList;
    private ImageView mPlayToggle;
    private AppCompatSeekBar mSeekBarProgress;
    private Handler mHandler = new Handler();
    public final int DELAY_TIME = 1000;
    private TextView mAudioPlayed;
    private TextView mAudioLeft;
    private ImageView mReplay;
    private ImageView mForward;
    private ImageView mIssueCategoryMenu;
    private ImageView mNext;
    private ImageView mArticleCoverImage;
    private ImageView mLast;
    private TextView mPlayFlyTitle;
    private TextView mAudioPlayTitle;
    private TextView mPlaySpeed;
    private Article mAudioPlayingArticle;
    private RecyclerView mAudioIssueCategoryRV;
    private ImageView mBarPlay;
    private AppCompatSeekBar mBarPlayingProgress;
    private ImageView mBarSkip;
    private ImageView mBarPlayingClose;
    private View.OnClickListener mPlayOrPauseListener = new View.OnClickListener() {
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
            if (progress >= 0 && progress <= mSeekBarProgress.getMax()) {
                mSeekBarProgress.setProgress(progress);
                mBarPlayingProgress.setProgress(progress);
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
            mAudioLeft.setText(Utils.getDurationFormat(mAudioPlayingArticle.audioDuration - progress / 1000));
        } else {
            mAudioLeft.setText(Utils.getDurationFormat(0));
        }
        mAudioPlayed.setText(Utils.getDurationFormat(progress / 1000));
    }

    public View getAudioPlayingBar() {
        if (getView() == null) {
            return null;
        }
        return getView().findViewById(R.id.audio_playing_bar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mContext = getContext();
        mArticleList = InchoateApp.getsAudioPlayingArticleListCache();
        mAudioPlayingArticle = mArticleList.get(0);
        EventBus.getDefault().register(this);
        mHandler.removeCallbacks(mProgressCallback);
        mHandler.postDelayed(mProgressCallback, 1000);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        initAudioPlayerFragmentView(view);
        initOnListener();
    }

    @Override
    protected void loadData() {
        initData();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_audio_play;
    }

    @Override
    protected <T> T fetchDataFromDBOrNetwork() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void initAudioPlayerFragmentView(View view) {
        mBarPlay = view.findViewById(R.id.bar_play);
        mBarPlayingProgress = view.findViewById(R.id.bar_playing_progress);
        mBarSkip = view.findViewById(R.id.bar_skip_15);
        mBarPlayingClose = view.findViewById(R.id.bar_playing_close);

        mLast = view.findViewById(R.id.last);
        mNext = view.findViewById(R.id.next);

        mArticleCoverImage = view.findViewById(R.id.article_cover_image);
        mPlayFlyTitle = view.findViewById(R.id.audio_play_fly_title);
        mAudioPlayTitle = view.findViewById(R.id.audio_play_title);
        mIssueCategoryMenu = view.findViewById(R.id.issue_category_menu);
        mAudioPlayed = view.findViewById(R.id.audio_played);
        mAudioLeft = view.findViewById(R.id.audio_left);

        mReplay = view.findViewById(R.id.replay);
        mForward = view.findViewById(R.id.forward);

        mPlayToggle = view.findViewById(R.id.play_toggle);
        mSeekBarProgress = view.findViewById(R.id.playing_progress);
        mPlaySpeed = view.findViewById(R.id.play_speed);
        mAudioIssueCategoryRV = view.findViewById(R.id.audio_issue_category_rv);
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
                .error(R.mipmap.the_economist)
                .timeout(10*1000)
                .placeholder(R.mipmap.the_economist)
                .into(mArticleCoverImage);
        mPlayFlyTitle.setText(mAudioPlayingArticle.flyTitle);
        mAudioPlayTitle.setText(mAudioPlayingArticle.title);
        mAudioPlayed.setText(Utils.getDurationFormat(0));
        mAudioLeft.setText(Utils.getDurationFormat(mAudioPlayingArticle.audioDuration));
        mSeekBarProgress.setMax((int) mAudioPlayingArticle.audioDuration * 1000);
        mBarPlayingProgress.setMax((int) mAudioPlayingArticle.audioDuration * 1000);
        mPlayToggle.setImageResource(R.mipmap.pause);
        mBarPlay.setImageResource(R.mipmap.pause);
    }

    public void initOnListener() {
        mBarPlay.setOnClickListener(mPlayOrPauseListener);

        mSeekBarProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mBarPlayingProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mPlayToggle.setOnClickListener(mPlayOrPauseListener);

        mBarPlayingClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                panelState.panelState = SlidingUpPanelLayout.PanelState.HIDDEN;
                EventBus.getDefault().post(panelState);
                EconomistPlayerTimberStyle.stop();
            }
        });

        mReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                        getActivity().getSharedPreferences(INCHOATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
                preferences.edit().putString(REWIND_OR_FORWARD_PREFERENCE, REWIND_BY_SECONDS_PREFERENCE).apply();
                EconomistPlayerTimberStyle.seekToIncrementPosition();
            }
        });

        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                        getActivity().getSharedPreferences(INCHOATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
                preferences.edit().putString(REWIND_OR_FORWARD_PREFERENCE, FORWARD_BY_SECONDS_PREFERENCE).apply();
                EconomistPlayerTimberStyle.seekToIncrementPosition();
            }
        });

        mLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EconomistPlayerTimberStyle.playPrevious();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
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
        mPlayToggle.setImageResource(isPlaying ? R.mipmap.pause : R.mipmap.play);
        mBarPlay.setImageResource(isPlaying ? R.mipmap.pause : R.mipmap.play);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mProgressCallback);
        EconomistPlayerTimberStyle.stop();
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
