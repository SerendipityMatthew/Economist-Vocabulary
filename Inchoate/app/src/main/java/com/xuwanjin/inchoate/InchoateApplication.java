package com.xuwanjin.inchoate;

import android.app.Application;
import android.os.StrictMode;

import androidx.navigation.NavController;

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
    public static Article cacheDisplayArticle ;
    @Override

    public void onCreate() {
        super.onCreate();
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

    public static void setNewestIssueCache(Issue issue){
        cacheNewestIssue.add(issue);
    }
    public static List<Issue> getNewestIssueCache(){
        return cacheNewestIssue;
    }
    public static void setDisplayArticleCache(Article article){
        cacheDisplayArticle = article;
    }
    public static Article getDisplayArticleCache(){
        return cacheDisplayArticle;
    }
}
