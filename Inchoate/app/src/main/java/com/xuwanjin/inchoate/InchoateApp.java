package com.xuwanjin.inchoate;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.navigation.NavController;

import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Matthew Xu
 */
public class InchoateApp extends Application {
    private static final String TAG = "InchoateApp";
    public static InchoateApp mInchoateApp;
    @SuppressLint("StaticFieldLeak")
    public static NavController NAVIGATION_CONTROLLER;
    public final static String ECONOMIST_URL = "";
    public static List<Issue> sCacheNewestIssue = new ArrayList<>(1);
    public static LinkedHashMap<String, List<Article>> sArticleLinkedHashMap = new LinkedHashMap<>();
    public static Article sCacheDisplayArticle;
    public static List<Article> sAudioPlayingArticleListCache;
    public static List<String> sCollectedVocabularyList;
    public static int SCROLL_TO_POSITION = -1;
    private ScheduledExecutorService mExecutorService = Executors.newScheduledThreadPool(2);
    Runnable mOpenDatabaseRunnable = new Runnable() {
        @Override
        public void run() {
            InchoateDBHelper helper = InchoateDBHelper.getInstance(getApplicationContext());
            helper.getReadableDatabase();
//                helper.close();
        }
    };

    Runnable mOpenVocabularyRunnable = new Runnable() {
        @Override
        public void run() {
            InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.oalecd_history_20200530);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str = null;
            HashSet<String> collectedVocabulary = new HashSet<>();

            try {
                while ((str = bufferedReader.readLine()) != null) {
                    collectedVocabulary.add(str);
                }
            } catch (IOException e) {

            }
            sCollectedVocabularyList = new ArrayList<>(collectedVocabulary);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mInchoateApp = this;

        mExecutorService.schedule(mOpenDatabaseRunnable, 3, TimeUnit.SECONDS);
        mExecutorService.schedule(mOpenVocabularyRunnable, 3, TimeUnit.SECONDS);
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy
//                .Builder()
//                .detectNetwork()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .penaltyDialog()
//                .detectDiskReads()
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedClosableObjects()
//                .detectActivityLeaks()
//                .penaltyLog()
//                .penaltyDeath()
//                .setClassInstanceLimit(InchoateActivity.class, 1)
//                .build());
    }



    public static void setNewestIssueCache(Issue issue) {
        sCacheNewestIssue.add(0,issue);
    }

    public static List<Issue> getNewestIssueCache() {
        return sCacheNewestIssue;
    }

    public static void setDisplayArticleCache(Article article) {
        sCacheDisplayArticle = article;
    }

    public static Article getDisplayArticleCache() {
        return sCacheDisplayArticle;
    }

    public static void setScrollToPosition(int position) {
        SCROLL_TO_POSITION = position;
    }

    public static int getScrollToPosition() {
        return SCROLL_TO_POSITION;
    }

    public static void setsAudioPlayingArticleListCache(List<Article> list) {
        sAudioPlayingArticleListCache = list;
    }

    public static List<Article> getsAudioPlayingArticleListCache() {
        return sAudioPlayingArticleListCache;
    }
}
