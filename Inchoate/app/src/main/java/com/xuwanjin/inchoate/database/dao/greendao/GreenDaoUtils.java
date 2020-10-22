package com.xuwanjin.inchoate.database.dao.greendao;

import android.content.Context;

import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.model.ArticleDao;
import com.xuwanjin.inchoate.model.IssueDao;
import com.xuwanjin.inchoate.model.ParagraphDao;
import com.xuwanjin.inchoate.model.VocabularyDao;

public class GreenDaoUtils {
    public static ArticleDao getArticleDao(Context context){
        if (context == null){
            return null;
        }
        return ((InchoateApp) (context.getApplicationContext())).getDaoSession().getArticleDao();
    }
    public static IssueDao getIssueDao(Context context){
        if (context == null){
            return null;
        }
        return ((InchoateApp) (context.getApplicationContext())).getDaoSession().getIssueDao();
    }
    public static ParagraphDao getParagraphDao(Context context){
        if (context == null){
            return null;
        }
        return ((InchoateApp) (context.getApplicationContext())).getDaoSession().getParagraphDao();
    }
    public static VocabularyDao getVocabularyDao(Context context){
        if (context == null){
            return null;
        }
        return ((InchoateApp) (context.getApplicationContext())).getDaoSession().getVocabularyDao();
    }
}
