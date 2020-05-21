package com.xuwanjin.inchoate;

import android.app.Application;
import android.os.StrictMode;

import androidx.navigation.NavController;

import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class InchoateApplication extends Application {
    public static InchoateApplication inchoateApp;
    public static NavController NAVIGATION_CONTROLLER;
    public final static String ECONOMIST_URL = "";
    public static List<Issue> cacheNewestIssue = new ArrayList<>(1);
    public static LinkedHashMap<String, List<Article>> sArticleLinkedHashMap = new LinkedHashMap<>();
    public static Article cacheDisplayArticle;
    public static List<Article> audioPlayingArticleListCache;
    public static int SCROLL_TO_POSITION = -1;

    @Override

    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                InchoateDBHelper helper = new InchoateDBHelper(getApplicationContext(), null, null);
                helper.getReadableDatabase();
            }
        }).start();
        inchoateApp = this;
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
        cacheNewestIssue.add(issue);
    }

    public static List<Issue> getNewestIssueCache() {
        return cacheNewestIssue;
    }

    public static void setDisplayArticleCache(Article article) {
        cacheDisplayArticle = article;
    }

    public static Article getDisplayArticleCache() {
        return cacheDisplayArticle;
    }

    public static void setScrollToPosition(int position) {
        SCROLL_TO_POSITION = position;
    }

    public static int getScrollToPosition() {
        return SCROLL_TO_POSITION;
    }

    public static void setAudioPlayingArticleListCache(List<Article> list) {
        audioPlayingArticleListCache = list;
    }

    public static List<Article> getAudioPlayingArticleListCache() {
        return audioPlayingArticleListCache;
    }
}
