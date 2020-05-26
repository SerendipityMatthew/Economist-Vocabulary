package com.xuwanjin.inchoate.timber_style;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.events.PlayEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xuwanjin.inchoate.Constants.ARTICLE_DETAIL_PLAYING_SOURCE;
import static com.xuwanjin.inchoate.Constants.BOOKMARK_PLAYING_SOURCE;
import static com.xuwanjin.inchoate.Constants.TODAY_PLAYING_SOURCE;
import static com.xuwanjin.inchoate.Constants.WEEKLY_PLAYING_SOURCE;

public class EconomistServiceTimberStyle extends Service {
    public static final String TAG = "EconomistServiceTimberStyle";
    private final IBinder mBinder = new ServiceStub(this);
    public static final int PLAY_NEXT_ARTICLE_AUDIO = 1000;
    private Issue mCurrentIssue;
    private static int mArticleInPlayingListIndex;
    private static Article mCurrentPlayingArticle;
    static ThreadPoolExecutor sAudioPlayingPoolExecutor =
            new ThreadPoolExecutor(1, 1, 5,
            TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());

    // 播放的列表, ① ArticleFragment 界面的 整个当前的一篇文章内容, 但是有 previous, next;
    //           ② BookmarkFragment 界面 bookmark 列表的内容, 从第一篇文章开始播放到最后一篇文章
    //           ③ TodayFragment 界面的单篇文章播放, 播放的列表是, Today 界面的所有能播放音频的文章.
    //           ④ WeeklyFragment 界面的播放整期内容
    // 数据都要从数据库里获取
    // 传递过来的标志从哪获取.
    private static List<Article> mCurrentPlayingArticleList = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    private Handler mServiceHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == PLAY_NEXT_ARTICLE_AUDIO) {
                play();
            }
        }
    };
    private EconomistPlayer mPlayer;
    private List<EconomistPlayer> economistPlayerListCache = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (economistPlayerListCache != null && economistPlayerListCache.size() > 0) {
            mPlayer = economistPlayerListCache.get(0);
        } else {
            mPlayer = new EconomistPlayer(EconomistServiceTimberStyle.this);
            economistPlayerListCache.add(0, mPlayer);
        }
        setPlayerHandler(mServiceHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: flags = " + flags);
        Log.d(TAG, "onStartCommand: intent = " + intent);
        Log.d(TAG, "onStartCommand: startId = " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    public void setPlayerHandler(Handler handler) {
        mPlayer.setHandler(handler);
    }

    public void setCurrentPlayingArticleListBySource(int sourceFlag) {
        InchoateDBHelper helper = new InchoateDBHelper(getApplicationContext(), null, null);
        if (sourceFlag == WEEKLY_PLAYING_SOURCE) {
            mCurrentPlayingArticleList = helper.queryIssueByIssueDate("").get(0).containArticle;
        }
        if (sourceFlag == BOOKMARK_PLAYING_SOURCE) {
            mCurrentPlayingArticleList = helper.queryBookmarkedArticle();
        }
        if (sourceFlag == TODAY_PLAYING_SOURCE) {

        }
        if (sourceFlag == ARTICLE_DETAIL_PLAYING_SOURCE) {

        }
        mPlayer.setArticleAudioPlayingList();
    }

    public void setArticleInPlayingListIndex(Article article) {
        for (int i = 0; i < mCurrentPlayingArticleList.size(); i++) {
            Article art = mCurrentPlayingArticleList.get(i);
            if (art.section.equals(article.section)
                    && art.title.equals(article.title)) {
                mArticleInPlayingListIndex = i;
            }
        }
    }


    public void setCurrentIssue(Issue issue) {
        Log.d(TAG, "setCurrentIssue: ");
        InchoateDBHelper helper = new InchoateDBHelper(getBaseContext(), null, null);
        List<Issue> issueList = helper.queryIssueByIssueDate(issue.issueDate);
        mCurrentIssue = issueList.get(0);
    }

    public void playTheRestOfWholeIssue(Article article) {
        for (Article art : mCurrentIssue.containArticle) {
            if (art != null && !art.audioUrl.trim().equals("")) { // 去掉 漫画 这个特殊的, 他没有音频文件
                mCurrentPlayingArticleList.add(art);
            }
        }
        setArticleInPlayingListIndex(article);
    }

    public void play() {
        mPlayer.play();
    }

    public void pause() {
        mPlayer.pause();
    }

    //
    public void playOrPause() {
        if (mPlayer == null) {
            return;
        }
        boolean isPlaying = mPlayer.isPlaying();
        if (isPlaying) {
            mPlayer.pause();
        } else {
            mPlayer.playFromPause();
        }
        isPlaying = mPlayer.isPlaying();
        updatePlayButtonAppearance(isPlaying);
    }

    public void updatePlayButtonAppearance(boolean isPlaying) {
        PlayEvent playEvent = new PlayEvent();
        playEvent.isPlaying = isPlaying;
        EventBus.getDefault().post(playEvent);
    }


    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        economistPlayerListCache.clear();
    }

    public void releasePlayer() {
        mPlayer.releasePlayer();
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public void playNext() {
        mPlayer.playNext();
    }

    public void playPrevious() {
        mPlayer.playPrevious();
    }

    public void playTheRestByIssueDate(Article article, String issueDate, int sourceFlag) {
        Log.d(TAG, "playTheRestByIssueDate: ");
        InchoateDBHelper helper = new InchoateDBHelper(getBaseContext(), null, null);
        List<Issue> issueList = helper.queryIssueByIssueDate(issueDate);
        if (helper != null) {
            helper.close();
        }
        mCurrentIssue = issueList.get(0);
        playTheRestOfWholeIssue(article);
        setCurrentPlayingArticleListBySource(sourceFlag);
        setArticleInPlayingListIndex(article);
    }

    public void seekToPosition(int position) {
        mPlayer.seekToPosition(position);
    }

    private static final class ServiceStub extends IEconomistService.Stub {
        private final WeakReference<EconomistServiceTimberStyle> mService;

        private ServiceStub(EconomistServiceTimberStyle service) {
            mService = new WeakReference<>(service);

        }

        @Override
        public void openFile(String filePath) throws RemoteException {
            mService.get().openFile(filePath);

        }

        @Override
        public void stop() throws RemoteException {

        }

        @Override
        public void pause() throws RemoteException {
            mService.get().pause();
        }

        @Override
        public void play() throws RemoteException {
            mService.get().play();
        }

        @Override
        public void playOrPause() throws RemoteException {
            mService.get().playOrPause();
        }

        @Override
        public void playTheRest(Article article, Issue issue) throws RemoteException {
            mService.get().setCurrentIssue(issue);
            mService.get().playTheRestOfWholeIssue(article);
            play();
        }

        @Override
        public void playTheRestByIssueDate(Article article, String issueDate, int sourceFlag) throws RemoteException {

            mService.get().playTheRestByIssueDate(article, issueDate, sourceFlag);
            play();
        }

        @Override
        public void next() throws RemoteException {
            mService.get().playNext();
        }

        @Override
        public void previous() throws RemoteException {
            mService.get().playPrevious();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mService.get().isPlaying();
        }

        @Override
        public void seekToPosition(int position) throws RemoteException {
            mService.get().seekToPosition(position);
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return mService.get().getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return mService.get().getDuration();
        }

        public void releasePlayer() throws RemoteException {
            mService.get().releasePlayer();
        }
    }

    public void openFile(String filePath) throws RemoteException {
        if (filePath == null) {
            return;
        }
        mPlayer.setDataSource(filePath);

    }

    // 输入内容 Article, 产生效果 -- 播放
    //    播放结束之后是否要播放接下来的文章
    public static final class EconomistPlayer implements MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnPreparedListener {
        private final WeakReference<EconomistServiceTimberStyle> mService;
        private MediaPlayer mMediaPlayer;
        private boolean isNetworkBuffering = true;
        private List<Article> mArticleList;
        private Handler mHandler;

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion: ");
            if (mp == mMediaPlayer) {
                if (mArticleList != null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    mMediaPlayer = new MediaPlayer();
                    mArticleInPlayingListIndex++;
                    mHandler.obtainMessage(PLAY_NEXT_ARTICLE_AUDIO).sendToTarget();
                }
            }
        }

        public void setArticleAudioPlayingList() {
            mArticleList = mCurrentPlayingArticleList;
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        public EconomistPlayer(final EconomistServiceTimberStyle service) {
            mService = new WeakReference<>(service);
        }

        public void setDataSource(String filePath) {
            Log.d(TAG, "setDataSource: mMediaPlayer = " + mMediaPlayer);
            try {
                mMediaPlayer.setDataSource(filePath);
            } catch (IOException e) {
                Log.d(TAG, "setDataSource: exception ");
                e.printStackTrace();
            }
        }

        public void play() {
            Runnable audioPlayingTask = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "EconomistPlayer: play: ");
                    if (mArticleInPlayingListIndex < 0 || mArticleInPlayingListIndex >= mArticleList.size()) {
                        return;
                    }
                    Article article = mArticleList.get(mArticleInPlayingListIndex);

                    if (article == null) {
                        return;
                    }

                    if (mMediaPlayer != null) {
                        mMediaPlayer.reset();
                    }
                    mMediaPlayer = new MediaPlayer();
                    String localCommonPath = "/storage/emulated/0/Android/data/";
                    String audioPath;
                    if (article.localeAudioUrl != null
                            && article.localeAudioUrl.startsWith(localCommonPath)) {
                        File file = new File(article.localeAudioUrl);
                        if (file.exists()) {
                            audioPath = article.localeAudioUrl;
                        } else {
                            audioPath = article.audioUrl;
                        }
                    } else {
                        audioPath = article.audioUrl;
                    }
                    mCurrentPlayingArticle = article;
                    Log.d(TAG, "play: audioPath = " + audioPath);
                    isNetworkBuffering = true;
                    try {
                        mMediaPlayer.setDataSource(audioPath);
                        mMediaPlayer.prepare();
                        mMediaPlayer.setOnCompletionListener(EconomistPlayer.this);
                        mMediaPlayer.setOnErrorListener(EconomistPlayer.this);
                        mMediaPlayer.setOnBufferingUpdateListener(EconomistPlayer.this);
                        mMediaPlayer.setOnPreparedListener(EconomistPlayer.this);
                        mMediaPlayer.start();
                        PlayEvent playEvent = new PlayEvent();
                        playEvent.isPlaySkip = true;
                        playEvent.mArticle = mCurrentPlayingArticle;
                        EventBus.getDefault().post(playEvent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            };
            sAudioPlayingPoolExecutor.submit(audioPlayingTask);
        }

        public void playFromPause() {
            Runnable audioPauseRunnable = new Runnable() {
                @Override
                public void run() {
                    mMediaPlayer.start();
                }
            };
            sAudioPlayingPoolExecutor.submit(audioPauseRunnable);
        }


        public boolean isPlaying() {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        }

        public void playNext() {
            mArticleInPlayingListIndex++;
            stop();
            play();

        }

        public void playPrevious() {
            mArticleInPlayingListIndex--;
            stop();
            play();
        }

        public void stop() {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }
        }

        public boolean isNetworkBuffering() {
            return isNetworkBuffering;
        }

        public int getCurrentPosition() {

            return mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
        }

        public int getDuration() {

            return mMediaPlayer == null ? -1 : mMediaPlayer.getDuration();
        }

        public void seekToPosition(int position) {
            if (mMediaPlayer != null) {
                mMediaPlayer.seekTo(position);
            }
        }

        public void releasePlayer() {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        public void pause() {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
            }
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            // 这个 percent 有数字了表示就开始正在网络缓存当中
            Log.d(TAG, "EconomistPlayer: onBufferingUpdate: percent = " + percent);
            Log.d(TAG, "EconomistPlayer: onBufferingUpdate: mp = " + mp);
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            // 这个 表示可以播放文件了
            isNetworkBuffering = false;
            Log.d(TAG, "EconomistPlayer: onPrepared: ");
        }
    }
}
