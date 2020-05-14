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

import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class EconomistServiceTimberStyle extends Service {
    public static final String TAG = "EconomistServiceTimberStyle";
    private final IBinder mBinder = new ServiceStub(this);
    public static final int PLAY_NEXT_ARTICLE_AUDIO = 1000;
    private ArrayDeque<Article> articleArrayDeque = new ArrayDeque<>();
    private Issue mCurrentIssue;

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

    public void setArticleArrayDeque(ArrayDeque<Article> deque) {
        articleArrayDeque = deque;
    }

    public void setPlayerHandler(Handler handler) {
        mPlayer.setHandler(handler);
    }

    public void setPlayerArticlePlayingDeque() {
        mPlayer.setArticleAudioPlayingDeque(articleArrayDeque);
    }

    public void setCurrentIssue(Issue issue) {
        mCurrentIssue = issue;
    }

    // 找出当前播放的文章在整个期刊里的位置, 然后取出后面的所有文章(包括当前的期刊) 作为一个播放列表,
    // 上一个播放结束之后, 在播放列表里取下一个播放文件
    public void playTheRestOfWholeIssue(Article article) {
        List<Article> articleList = mCurrentIssue.containArticle;
        if (articleList == null || articleList.size() == 0) {
            return;
        }
        int position = -1;
        for (int i = 0; i < articleList.size(); i++) {
            Article iterArticle = articleList.get(i);
            if (iterArticle.section.equals(article.section)
                    && iterArticle.title.equals(article.title)) {
                position = i;
            }
        }
        List<Article> leftOverArticle = articleList.subList(position, articleList.size());
        ArrayDeque<Article> articleArrayDeque = new ArrayDeque<>();
        for (Article art : leftOverArticle) {
            if (art.audioUrl != null && !art.audioUrl.trim().equals("")) { // 去掉 漫画 这个特殊的, 他没有音频文件
//                Log.d(TAG, "playTheRestOfWholeIssue:  art.title = " + art.title);
                articleArrayDeque.addLast(art);
            }
        }
        setArticleArrayDeque(articleArrayDeque);
        setPlayerArticlePlayingDeque();
    }

    public void play() {
        mPlayer.play();
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

        }

        @Override
        public void play() throws RemoteException {
            mService.get().play();
        }

        @Override
        public void playTheRest(Article article, Issue issue) throws RemoteException {
            mService.get().setCurrentIssue(issue);
            mService.get().playTheRestOfWholeIssue(article);
            play();
        }

        @Override
        public void next() throws RemoteException {

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
            MediaPlayer.OnPreparedListener
    {
        private final WeakReference<EconomistServiceTimberStyle> mService;
        private MediaPlayer mMediaPlayer;
        private boolean isNetworkBuffering = true;
        private MediaPlayer mNextMediaPlayer;
        private ArrayDeque<Article> mArticlePlayingDeque;
        private Handler mHandler;

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion: ");
            if (mp == mMediaPlayer) {
                if (mArticlePlayingDeque != null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    mMediaPlayer = new MediaPlayer();
                    mHandler.obtainMessage(PLAY_NEXT_ARTICLE_AUDIO).sendToTarget();
                }
            }
        }

        public void setArticleAudioPlayingDeque(ArrayDeque<Article> deque) {
            mArticlePlayingDeque = deque;
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
                e.printStackTrace();
            }
        }

        public void play() {
            synchronized (this) {
                Log.d(TAG, "EconomistPlayer: play: ");
                try {
                    Article article = mArticlePlayingDeque.poll();
                    Log.d(TAG, "play: mArticlePlayingDeque.size() = " + mArticlePlayingDeque.size());
                    if (article == null) {
                        return;
                    }

                    if (mMediaPlayer != null) {
                        mMediaPlayer.reset();
                    }
                    mMediaPlayer = new MediaPlayer();
                    setDataSource(article.audioUrl);
                    isNetworkBuffering = true;
                    mMediaPlayer.prepare();
                    mMediaPlayer.setOnCompletionListener(this);
                    mMediaPlayer.setOnErrorListener(this);
                    mMediaPlayer.setOnBufferingUpdateListener(this);
                    mMediaPlayer.setOnPreparedListener(this);
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isPlaying() {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
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
