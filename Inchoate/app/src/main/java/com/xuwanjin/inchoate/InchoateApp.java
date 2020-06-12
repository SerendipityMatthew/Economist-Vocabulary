package com.xuwanjin.inchoate;

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

public class InchoateApp extends Application {
    private static final String TAG = "InchoateApp";
    public static InchoateApp mInchoateApp;
    public static NavController NAVIGATION_CONTROLLER;
    public final static String ECONOMIST_URL = "";
    public static List<Issue> sCacheNewestIssue = new ArrayList<>(1);
    public static LinkedHashMap<String, List<Article>> sArticleLinkedHashMap = new LinkedHashMap<>();
    public static Article sCacheDisplayArticle;
    public static List<Article> sAudioPlayingArticleListCache;
    public static List<String> sCollectedVocabularyList;
    public static int SCROLL_TO_POSITION = -1;

    @Override

    public void onCreate() {
        super.onCreate();
        Runnable openDatabaseRunnable = new Runnable() {
            @Override
            public void run() {
                InchoateDBHelper helper = InchoateDBHelper.getInstance(getApplicationContext());
                helper.getReadableDatabase();
//                helper.close();
            }
        };

        Executors.newSingleThreadExecutor().submit(openDatabaseRunnable);
        mInchoateApp = this;
        Runnable openVocabularyRunnable = new Runnable() {
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
        Executors.newSingleThreadExecutor().submit(openVocabularyRunnable);
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
