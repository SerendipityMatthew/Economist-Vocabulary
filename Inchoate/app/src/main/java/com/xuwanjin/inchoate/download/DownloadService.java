package com.xuwanjin.inchoate.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// 输入 issue 下载整个期刊
// 输入 Article 下载一篇文章

//下载每篇文章的音频文件
// 下载完成后更新数据库字段
public class DownloadService extends Service {
    public static final String TAG = "DownloadService";
    private final IBinder mBinder = new LocalBinder();
    private Issue mIssue = new Issue();
    private List<Article> downloadArticle = new ArrayList<>();
    public static int percent = 0;
    public ArrayMap<String, Boolean> localAudioUrlMap = new ArrayMap<>();
    final Runnable mDownloadRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mDownloadRunnable: run: ");
            ArrayList<Article> audioArticle = new ArrayList<>();
            for (Article article : downloadArticle) {
                if (article.audioUrl != null
                        && !article.audioUrl.trim().equals("")) {
                    audioArticle.add(article);
                }
            }

            File commonFile = getBaseContext().getExternalCacheDirs()[0];

            //     issueDate/Section/article_title
            //N 个     article_audio_url
            //
            final String issueDate = mIssue.issueDate;
            DownloadTask[] downloadTasks = new DownloadTask[audioArticle.size()];
            for (int i = 0; i < audioArticle.size(); i++) {
                Article article = audioArticle.get(i);
                String section = article.section;
                String audioFile = commonFile.getAbsolutePath() + "/" + issueDate + "/" + section;
                String noSpacePath = audioFile.replace(" ", "_");
                String noSpaceName = article.title.replace(" ", "_") + ".mp3";
                DownloadTask task =
                        new DownloadTask
                                .Builder(article.audioUrl, noSpacePath, noSpaceName)
                                .build()
                                .addTag(12, article);
                downloadTasks[i] = task;
                String fullPath = noSpacePath + noSpaceName;
                localAudioUrlMap.put(fullPath, false);
                Log.d(TAG, "mDownloadRunnable: run: ");
            }
            DownloadTask.enqueue(downloadTasks, downloadListener);
        }
    };

    private static final ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String formatIssueDate = intent.getStringExtra(Constants.PENDING_DOWNLOAD_ISSUE_DATE);
        InchoateDBHelper helper = new InchoateDBHelper(getApplicationContext(), null, null);
        mIssue = helper.queryIssueByFormatIssueDate(formatIssueDate).get(0);
        Article article = intent.getParcelableExtra(Constants.DOWNLOAD_ARTICLE);
        Log.d(TAG, "onStartCommand: mIssue = " + mIssue);
        if (mIssue != null && article == null) {
            downloadArticle = mIssue.containArticle;
        }
        if (mIssue == null && article != null) {
            downloadArticle.add(article);
        }
        if (mIssue != null && article != null) {
            downloadArticle = mIssue.containArticle;
        }
        mExecutorService.submit(mDownloadRunnable);
        if (mIssue == null && article == null) {

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public float getDownloadPercent() {
        if (localAudioUrlMap == null || !(localAudioUrlMap.size()>0)){
            return 0;
        }
        int count = 0;
        for (int i = 0; i < localAudioUrlMap.size();i++ ){
            if (localAudioUrlMap.valueAt(i)){
                count++;
            }
        }
        float percent = (float) ((count*100.0)/localAudioUrlMap.size());
        Log.d(TAG, "getDownloadPercent: percent = " + percent);
        return percent;
    }

    final DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void taskStart(@NonNull DownloadTask task) {
            Log.d(TAG, "taskStart: task.getFilename = " + task.getFilename());

        }

        @Override
        public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {

        }

        @Override
        public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

        }

        @Override
        public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {

        }

        @Override
        public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {

        }

        @Override
        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

        }

        @Override
        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

        }

        @Override
        public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {

        }

        @Override
        public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {

        }

        @Override
        public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {

        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
            String localeAudioUrl = task.getParentFile().getAbsolutePath() + "/" + task.getFilename();
            InchoateDBHelper helper = new InchoateDBHelper(getBaseContext(), null, null);
            Article article = (Article) task.getTag(12);
            article.localeAudioUrl = localeAudioUrl;
            Log.d(TAG, "taskEnd: localeAudioUrl = " + localeAudioUrl);
            helper.updateArticleAudioLocaleUrl(article, article.date);
            localAudioUrlMap.put(localeAudioUrl, true);
        }
    };
}
