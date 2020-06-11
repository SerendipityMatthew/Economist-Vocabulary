package com.xuwanjin.inchoate.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.model.Vocabulary;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class InchoateDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "InchoateDBHelper";
    public static final String DATABASE_NAME = "inchoate.db";
    static final String TABLE_NAME_ISSUE = "issue";
    static final String TABLE_NAME_ARTICLE = "article";
    static final String TABLE_NAME_PARAGRAPH = "paragraph";
    static final String TABLE_NAME_VOCABULARY = "vocabulary";
    public Context mContext;
    private static volatile SQLiteDatabase sDatabase;
    private static AtomicInteger mInchoateDBCounter = new AtomicInteger();
    Disposable mDisposable;
    private static final long RECORD_NOT_EXISTED_IN_DB = -1000;
    private static final String KEY_ID = "id";

    // issue of table
    private static final String KEY_COVER_IMAGE_URL = "cover_image_url";
    private static final String KEY_IS_DOWNLOADED = "is_downloaded";
    private static final String KEY_ISSUE_URL = "issue_url";
    private static final String KEY_ISSUE_FORMAT_DATE = "issue_format_date";
    private static final String KEY_ISSUE_HEADLINE = "issue_headline";
    private static final String KEY_ISSUE_URL_ID = "url_id";
    // paragraph of article
    // the main key of table article
    private static final String KEY_ISSUE_DATE = "issue_date";
    private static final String KEY_SECTION = "section";
    private static final String KEY_TITLE = "title";

    private static final String KEY_FLYTITLE = "flytitle";
    private static final String KEY_ARTICLE_URL = "article_url";
    private static final String KEY_AUDIO_URL = "audio_url";
    private static final String KEY_LOCALE_AUDIO_URL = "locale_audio_url";
    private static final String KEY_MAIN_ARTICLE_IMAGE = "main_article_image";
    private static final String KEY_ARTICLE_IMAGE = "article_image";
    private static final String KEY_ARTICLE_RUBRIC = "article_rubric";
    private static final String KEY_AUDIO_DURATION = "audio_duration";
    private static final String KEY_IS_BOOKMARK = "is_bookmark";
    private static final String KEY_ISSUE_ID = "issue_id";


    //  table  of paragraph
    private static final String KEY_IS_EDITORS_NOTE = "is_editors_note";
    private static final String KEY_IS_RELATED_SUGGESTION = "is_related_suggestion";
    private static final String KEY_PARAGRAPH_CONTENT = "paragraph_content";
    private static final String KEY_ORDER_OF_PARAGRAPH = "order_of_paragraph";
    private static final String KEY_BELONGED_ARTICLE_ID = "belonged_article_id";


    //  table of Vocabulary
    private static final String KEY_VOCABULARY_CONTENT = "vocabulary_content";
    private static final String KEY_COLLECTED_DATE = "collected_date";
    private static final String KEY_COLLECTED_TIME = "collected_time";
    private static final String KEY_BELONGED_SENTENCE = "belonged_sentence";
    private static final String KEY_BELONGED_PARAGRAPH = "belonged_paragraph";
    private static final String KEY_BELONGED_ARTICLE_TITLE = "belonged_article_title";
    private static final String KEY_BELONGED_SECTION_NAME = "belonged_section_name";
    private static final String KEY_BELONGED_ISSUE_DATE = "belonged_issue_date";
    private static final String KEY_BELONGED_ARTICLE_URL = "belonged_article_url";

    private static final String KEY_ID_PARA = KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,";

    private static final String CREATE_TABLE_ISSUE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_ISSUE + " ( " + KEY_ID_PARA
            + KEY_ISSUE_DATE + " TEXT,"
            + KEY_COVER_IMAGE_URL + " TEXT,"
            + KEY_IS_DOWNLOADED + " TEXT,"
            + KEY_ISSUE_URL + " TEXT,"
            + KEY_ISSUE_HEADLINE + " TEXT,"
            + KEY_ISSUE_URL_ID + " TEXT,"
            + KEY_ISSUE_FORMAT_DATE + " DATE"
            + ")";
    private static final String CREATE_TABLE_ARTICLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_ARTICLE + " ( " + KEY_ID_PARA
            + KEY_ISSUE_DATE + " TEXT,"
            + KEY_SECTION + " TEXT,"
            + KEY_TITLE + " TEXT,"
            + KEY_ARTICLE_URL + " TEXT,"
            + KEY_AUDIO_URL + " TEXT,"
            + KEY_LOCALE_AUDIO_URL + " TEXT,"
            + KEY_FLYTITLE + " TEXT,"
            + KEY_MAIN_ARTICLE_IMAGE + " TEXT,"
            + KEY_ARTICLE_IMAGE + " TEXT,"
            + KEY_ARTICLE_RUBRIC + " TEXT,"
            + KEY_AUDIO_DURATION + " TEXT,"
            + KEY_IS_BOOKMARK + " TEXT,"
            + KEY_ISSUE_ID + " INTEGER"
            + ");";
    private static final String CREATE_TABLE_PARAGRAPH = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_PARAGRAPH + " ( " + KEY_ID_PARA
            + KEY_IS_EDITORS_NOTE + " TEXT,"
            + KEY_IS_RELATED_SUGGESTION + " TEXT,"
            + KEY_PARAGRAPH_CONTENT + " TEXT,"
            + KEY_ORDER_OF_PARAGRAPH + " INTEGER,"
            + KEY_BELONGED_ARTICLE_ID + " INTEGER"
            + ");";

    private static final String CREATE_TABLE_VOCABULARY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_VOCABULARY + " ( " + KEY_ID_PARA
            + KEY_VOCABULARY_CONTENT + " TEXT,"
            + KEY_COLLECTED_DATE + " TEXT,"
            + KEY_COLLECTED_TIME + " TEXT,"
            + KEY_BELONGED_SENTENCE + " TEXT,"
            + KEY_BELONGED_PARAGRAPH + " TEXT,"
            + KEY_BELONGED_ARTICLE_TITLE + " TEXT,"
            + KEY_BELONGED_SECTION_NAME + " TEXT,"
            + KEY_BELONGED_ISSUE_DATE + " TEXT,"
            + KEY_BELONGED_ARTICLE_URL + " TEXT"
            + ");";

    // 创建索引
    private static final String CREATE_TABLE_ISSUE_INDEX = "CREATE TABLE "
            + TABLE_NAME_ISSUE + " (" + " ON ";

    public synchronized SQLiteDatabase openInchoateDB() {
        if (sDatabase == null || !sDatabase.isOpen() || !sDatabase.isReadOnly()) {
            if (mInchoateDBCounter.incrementAndGet() == 1) {
                sDatabase = getWritableDatabase();
            }
        }
        return sDatabase;
    }

    public synchronized void closeInchoateDB() {
        if (mInchoateDBCounter.decrementAndGet() == 0) {
            if (sDatabase != null) {
                sDatabase.close();
            }
        }
    }

    public InchoateDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, 100);
        this.mContext = context;
    }

    public InchoateDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, version, errorHandler);
        this.mContext = context;
    }

    public void setBookmarkStatus(Article article, boolean isBookmark) {
        sDatabase = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_IS_BOOKMARK, isBookmark);
        sDatabase.update(TABLE_NAME_ARTICLE, contentValues, KEY_ID + "=?", new String[]{String.valueOf(article.rowIdInDB)});
        closeInchoateDB();
    }

    public List<Article> queryBookmarkedArticle() {
         sDatabase = openInchoateDB();
        // Select * from article where is_bookmark='true';
        String query = "SELECT * FROM " + TABLE_NAME_ARTICLE + " WHERE " + KEY_IS_BOOKMARK + " =\'0\'";
        Cursor cursor = sDatabase.rawQuery(query, null);

        List<Article> articleList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Article article = getArticleFromCursor(cursor);
            articleList.add(article);
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        if (cursor != null) {
            cursor.close();
        }
        return articleList;
    }

    public List<Issue> queryIssueByFormatIssueDate(String formatIssueDate) {
        List<Issue> issueList = new ArrayList<>();
         sDatabase = openInchoateDB();
        // Select * from article where issue_date='';
        String query = "SELECT * FROM " + TABLE_NAME_ISSUE + " WHERE " + KEY_ISSUE_FORMAT_DATE + " =\'" + formatIssueDate + "\'";
        Log.d(TAG, "queryIssueByFormatIssueDate: query = " + query);
        Cursor cursor = sDatabase.rawQuery(query, null);
        while (cursor != null && cursor.moveToNext()) {
            Issue issue;
            issue = getIssueFromCursor(cursor);
            issueList.add(issue);
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        if (cursor != null) {
            cursor.close();
        }
        return issueList;
    }

    public List<Issue> queryIssueByIssueDate(String issueDate) {
        List<Issue> issueList = new ArrayList<>();
        sDatabase = openInchoateDB();
        // Select * from article where issue_date='';
        String query = "SELECT * FROM " + TABLE_NAME_ISSUE + " WHERE " + KEY_ISSUE_DATE + " =\'" + issueDate + "\'";
        Log.d(TAG, "queryIssueByIssueDate: query = " + query);
        Cursor cursor = sDatabase.rawQuery(query, null);
        while (cursor != null && cursor.moveToNext()) {
            Issue issue;
            issue = getIssueFromCursor(cursor);
            issueList.add(issue);
        }
        if (cursor != null) {
            cursor.close();
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }

        return issueList;
    }

    public List<Issue> queryAllIssue() {
        List<Issue> issueList = new ArrayList<>();
        sDatabase = openInchoateDB();
        // Select * from article where issue_date='';
        String query = "SELECT * FROM " + TABLE_NAME_ISSUE;
        Log.d(TAG, "queryIssueByIssueDate: query = " + query);
        Cursor cursor = sDatabase.rawQuery(query, null);
        while (cursor != null && cursor.moveToNext()) {
            Issue issue;
            issue = getIssueFromCursor(cursor);
            issueList.add(issue);
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        if (cursor != null) {
            cursor.close();
        }
        return issueList;
    }


    public Cursor queryArticleByIssueDateResultCursor(String issueDate) {
        // //        "May 9th 2020"
        sDatabase = openInchoateDB();
        // Select * from article where issue_date='' and section='' and title = '' ;
        String query = "SELECT * FROM " + TABLE_NAME_ARTICLE + " WHERE "
                + KEY_ISSUE_DATE + " =\'" + issueDate + "\'";
        Cursor cursor = sDatabase.rawQuery(query, null);
        return cursor;
    }

    public Cursor queryArticleByIssueDate(String issueDate) {
        sDatabase = openInchoateDB();
        // Select * from article where issue_date='' ;
        String query = "SELECT * FROM " + TABLE_NAME_ARTICLE + " WHERE "
                + KEY_ISSUE_DATE + " =\'" + issueDate + "\'";
        Cursor cursor = sDatabase.rawQuery(query, null);

        return cursor;
    }

    private Cursor queryArticleByIssueDateSectionTitle(Article article, String issueDate) {
        sDatabase = openInchoateDB();
        // Select * from article where issue_date='' and section='' and title = '' ;
        String query = "SELECT * FROM " + TABLE_NAME_ARTICLE + " WHERE "
                + KEY_ISSUE_DATE + " =\'" + issueDate + "\'" + " AND "
                + KEY_SECTION + " =\'" + article.section + "\'" + " AND "
                + KEY_TITLE + " =\'" + article.title + "\' ";
        Cursor cursor = sDatabase.rawQuery(query, null);
        return cursor;
    }

    // Editor’s note: The Economist is making some of its most important coverage of the covid-19 pandemic freely
    // available to readers of The Economist Today
    //对于像这样的段落, 机会每一篇文章都有. 需要 articleRowID 和 Paragraph 内容同时确定
    private List<Paragraph> queryParagraphByContentAndArticleID(String content, long articleRowID) {
        // 如果 Paragraph 的表里查不出一个含有 articleRowID 的数据,
        // 表示这个 article 的 paragraph 没有被存入过
       sDatabase = openInchoateDB();
        if (content.contains("'")) {
            content = content.replace("'", "''");
        }
        // Select * from paragraph where belonged_article_id='' or paragraph_content='';
        String queryByArticleID = "SELECT * FROM " + TABLE_NAME_PARAGRAPH + " WHERE "
                + KEY_BELONGED_ARTICLE_ID + " =\'" + articleRowID + "\'" + " AND "
                + KEY_PARAGRAPH_CONTENT + "=\'" + content + "\'";
        Cursor cursor = sDatabase.rawQuery(queryByArticleID, null);
        List<Paragraph> paragraphList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Paragraph paragraph = getParagraphFromCursor(cursor);
            paragraphList.add(paragraph);
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        if (cursor != null) {
            cursor.close();
        }
        return paragraphList;
    }

    private long insertIssueData(Issue issue) {
       sDatabase = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_COVER_IMAGE_URL, issue.coverImageUrl);
        contentValues.put(KEY_IS_DOWNLOADED, issue.isDownloaded);
        contentValues.put(KEY_ISSUE_URL, issue.issueUrl);
        contentValues.put(KEY_ISSUE_DATE, issue.issueDate);
        contentValues.put(KEY_ISSUE_FORMAT_DATE, issue.issueFormatDate);
        contentValues.put(KEY_ISSUE_HEADLINE, issue.headline);
        long rowID = sDatabase.insert(TABLE_NAME_ISSUE, null, contentValues);
        if (sDatabase != null) {
            closeInchoateDB();
        }
        return rowID;
    }

    public List<Long> insertIssueDataByBatch(List<Issue> issueList) {
        ArrayList<Long> longList = new ArrayList<>();
        for (Issue issue : issueList) {
            longList.add(insertIssueData(issue));
        }
        return longList;
    }

    public long insertArticleData(Article article, long issueRowId, String issueDate) {
       sDatabase = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ISSUE_DATE, issueDate);
        contentValues.put(KEY_SECTION, article.section);
        contentValues.put(KEY_TITLE, article.title);

        contentValues.put(KEY_FLYTITLE, article.flyTitle);
        contentValues.put(KEY_ARTICLE_URL, article.articleUrl);
        contentValues.put(KEY_AUDIO_URL, article.audioUrl);
        contentValues.put(KEY_LOCALE_AUDIO_URL, article.articleUrl);
        contentValues.put(KEY_MAIN_ARTICLE_IMAGE, article.mainArticleImage);

        contentValues.put(KEY_ARTICLE_RUBRIC, article.articleRubric);
        contentValues.put(KEY_AUDIO_DURATION, article.audioDuration);
        contentValues.put(KEY_IS_BOOKMARK, article.isBookmark);
        contentValues.put(KEY_ISSUE_ID, issueRowId);
        long rowID = sDatabase.insert(TABLE_NAME_ARTICLE, null, contentValues);
        if (sDatabase != null) {
            closeInchoateDB();
        }
        return rowID;
    }

    public List<Vocabulary> getVocabularyList(String vocabularyContent) {
       sDatabase = openInchoateDB();
        // Select * from vocabulary where vocabulary_content='';
        String queryByArticleID = "SELECT * FROM " + TABLE_NAME_VOCABULARY + " WHERE "
                + KEY_VOCABULARY_CONTENT + " =\'" + vocabularyContent + "\'";
        Cursor cursor = sDatabase.rawQuery(queryByArticleID, null);
        List<Vocabulary> vocabularyList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Vocabulary vocabulary = getVocabularyFromCursor(cursor);
            vocabularyList.add(vocabulary);
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        return vocabularyList;
    }

    public boolean isVocabularyExistedInDB(String vocabularyContent, int limit) {
       sDatabase = openInchoateDB();
        // Select * from vocabulary where vocabulary_content='';
        String queryByArticleID = "SELECT * FROM " + TABLE_NAME_VOCABULARY + " WHERE "
                + KEY_VOCABULARY_CONTENT + " =\'" + vocabularyContent + "\'"
                + " LIMIT " + limit;
        Cursor cursor = sDatabase.rawQuery(queryByArticleID, null);
        List<Vocabulary> vocabularyList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Vocabulary vocabulary = getVocabularyFromCursor(cursor);
            vocabularyList.add(vocabulary);
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        if (cursor != null) {
            cursor.close();
        }
        if (vocabularyList != null && vocabularyList.size() > 0) {
            return true;
        }
        return false;
    }

    public long insertVocabulary(Vocabulary vocabulary) {
       sDatabase = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_VOCABULARY_CONTENT, vocabulary.vocabularyContent);
        contentValues.put(KEY_COLLECTED_DATE, vocabulary.collectedDate);
        contentValues.put(KEY_COLLECTED_TIME, vocabulary.collectedTime);
        contentValues.put(KEY_BELONGED_SENTENCE, vocabulary.belongedSentence);
        contentValues.put(KEY_BELONGED_PARAGRAPH, vocabulary.belongedParagraph);
        contentValues.put(KEY_BELONGED_ARTICLE_TITLE, vocabulary.belongedArticleTitle);
        contentValues.put(KEY_BELONGED_SECTION_NAME, vocabulary.belongedSectionName);
        contentValues.put(KEY_BELONGED_ISSUE_DATE, vocabulary.belongedIssueDate);
        contentValues.put(KEY_BELONGED_ARTICLE_URL, vocabulary.belongedArticleUrl);
        long rowID = sDatabase.insert(TABLE_NAME_VOCABULARY, null, contentValues);
        if (sDatabase != null) {
            closeInchoateDB();
        }
        return rowID;
    }

    public long updateArticleAudioLocaleUrl(Article article, String issueDate) {
        if (!isArticleExistedInDB(article, issueDate)) {
            Log.d(TAG, "updateArticleAudioLocaleUrl: ");
            return RECORD_NOT_EXISTED_IN_DB;
        }
       sDatabase = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ISSUE_DATE, issueDate);
        contentValues.put(KEY_SECTION, article.section);
        contentValues.put(KEY_TITLE, article.title);

        contentValues.put(KEY_FLYTITLE, article.flyTitle);
        contentValues.put(KEY_ARTICLE_URL, article.articleUrl);
        contentValues.put(KEY_AUDIO_URL, article.audioUrl);
        contentValues.put(KEY_LOCALE_AUDIO_URL, article.localeAudioUrl);
        contentValues.put(KEY_MAIN_ARTICLE_IMAGE, article.mainArticleImage);

        contentValues.put(KEY_ARTICLE_RUBRIC, article.articleRubric);
        contentValues.put(KEY_AUDIO_DURATION, article.audioDuration);
        contentValues.put(KEY_IS_BOOKMARK, article.isBookmark);
        String whereClause = KEY_ID + "=?";
        long affectedRowCount = sDatabase.update(TABLE_NAME_ARTICLE, contentValues, whereClause, new String[]{String.valueOf(article.rowIdInDB)});
        Log.d(TAG, "updateArticleAudioLocaleUrl: affectedRowCount = " + affectedRowCount);
        if (sDatabase != null) {
            closeInchoateDB();
        }
        return affectedRowCount;
    }

    public long insertParagraphData(Paragraph paragraph, long articleRowID) {
       sDatabase = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_IS_EDITORS_NOTE, paragraph.isEditorsNote);
        contentValues.put(KEY_IS_RELATED_SUGGESTION, paragraph.isRelatedSuggestion);
        contentValues.put(KEY_PARAGRAPH_CONTENT, paragraph.paragraph.toString());
        contentValues.put(KEY_ORDER_OF_PARAGRAPH, paragraph.theOrderOfParagraph);
        contentValues.put(KEY_BELONGED_ARTICLE_ID, articleRowID);
        long affectedRowCount = sDatabase.insert(TABLE_NAME_PARAGRAPH, null, contentValues);
        if (sDatabase != null) {
            closeInchoateDB();
        }
        return affectedRowCount;
    }

    // 查看是否存在, 如果存在,                获取 rowID
    //              如果不存在. 则插入数据,   并且返回 rowID;
    public Disposable insertWholeData(final Issue issue) {
        long rowID = issueRowIDInDB(issue);
        long issueRowID = RECORD_NOT_EXISTED_IN_DB;
        if (rowID == RECORD_NOT_EXISTED_IN_DB) {
            issueRowID = insertIssueData(issue);
        } else {
            issueRowID = rowID;
        }
        // issue 插入之后, 获取了issue 的 id,
        // 对 List<Article> 里面的每一项执行 insertArticleData操作,
        //              返回一个结果, 获取了文章的 id之后,
        //                      执行 Paragraph List 里的每一项的插入
        long finalIssueRowID = issueRowID;
        mDisposable = Flowable
                .fromIterable(issue.containArticle)        // 把 list 的元素一个一个的发送
                .map(new Function<Article, HashMap<Long, Article>>() {
                    @Override
                    public HashMap<Long, Article> apply(Article article) throws Exception {
                        Log.d(TAG, "insertWholeData: apply: article.title = " + article.title);
                        long id = articleRowIDInDB(article, issue.issueDate);
                        long articleRowID = RECORD_NOT_EXISTED_IN_DB;
                        if (id == RECORD_NOT_EXISTED_IN_DB) {
                            articleRowID = insertArticleData(article, finalIssueRowID, issue.issueDate);
                        } else {
                            articleRowID = id;
                        }
                        HashMap<Long, Article> articleHashMap = new HashMap<>(0);
                        articleHashMap.put(articleRowID, article);
                        return articleHashMap;
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "insertWholeData: doOnError: accept: ");
                        return;
                    }
                })
                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io()) // subscribeOn 和 observeOn 在同一个线程, 可以解决 database connection pool has been closed 问题, 所以注释掉这一条
                .subscribe(new Consumer<HashMap<Long, Article>>() {
                    @Override
                    public void accept(HashMap<Long, Article> longArticleHashMap) throws Exception {
                        long articleRowID = longArticleHashMap.keySet().iterator().next();
                        Article article = longArticleHashMap.values().iterator().next();
//                        Log.d(TAG, "insertWholeData: accept: articleRowID = " + articleRowID + " article.date =" + article.date);
                        for (Paragraph paragraph : article.paragraphList) {
                            long paragraphID = paragraphRowIDInDB(paragraph.paragraph.toString(), articleRowID);
                            if (paragraphID == RECORD_NOT_EXISTED_IN_DB) {
                                insertParagraphData(paragraph, articleRowID);
                            }
                        }
                    }
                });
        return mDisposable;
    }

    private boolean isArticleExistedInDB(Article article, String issueDate) {
        long rowID = articleRowIDInDB(article, issueDate);
        if (rowID == RECORD_NOT_EXISTED_IN_DB) {
            return false;
        }
        return true;
    }


    private long articleRowIDInDB(Article article, String issueDate) {
       sDatabase = openInchoateDB();
        // Select * from article where issue_date='' and section='' and title = '' ;

        String articleTitle = article.title;
        if (article.title.contains("'")) {
            articleTitle = articleTitle.replace("'", "''");
        }
        String query = "SELECT id FROM " + TABLE_NAME_ARTICLE + " WHERE "
                + KEY_ISSUE_DATE + " =\'" + issueDate + "\'" + " AND "
                + KEY_SECTION + " =\'" + article.section + "\'" + " AND "
                + KEY_TITLE + " =\'" + articleTitle + "\' ";
//        Log.d(TAG, "articleRowIDInDB: query = " + query);
        Cursor cursor = sDatabase.rawQuery(query, null);
        List<Article> articleList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Article a = getArticleIDFromCursor(cursor);
            articleList.add(a);
        }
        if (articleList.size() == 0) {
            return RECORD_NOT_EXISTED_IN_DB;
        }
        if (articleList.size() == 1) {
            return articleList.get(0).rowIdInDB;
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        if (cursor != null) {
            cursor.close();
        }
        return RECORD_NOT_EXISTED_IN_DB;
    }

    private long issueRowIDInDB(Issue issue) {
        List<Issue> issueList = queryIssueByIssueDate(issue.issueDate);
        if (issueList == null || issueList.size() == 0) {
            return RECORD_NOT_EXISTED_IN_DB;
        }
        if (issueList.size() == 1) {
            return issueList.get(0).id;
        }
        return RECORD_NOT_EXISTED_IN_DB;
    }

    private long paragraphRowIDInDB(String paragraphContent, long articleRowID) {
        List<Paragraph> paragraphList = queryParagraphByContentAndArticleID(paragraphContent, articleRowID);
        if (paragraphList == null || paragraphList.size() == 0) {
            return RECORD_NOT_EXISTED_IN_DB;
        }
        if (paragraphList.size() == 1) {
            return paragraphList.get(0).id;
        }

        return RECORD_NOT_EXISTED_IN_DB;
    }

    private Issue getIssueFromCursor(Cursor cursor) {
        int issueDateIndex = cursor.getColumnIndex(KEY_ISSUE_DATE);
        int issueIDIndex = cursor.getColumnIndex(KEY_ID);
        int issueURLIndex = cursor.getColumnIndex(KEY_ISSUE_URL);
        int issueCoverImageUrlIndex = cursor.getColumnIndex(KEY_COVER_IMAGE_URL);
        int issueIsDownloadedIndex = cursor.getColumnIndex(KEY_IS_DOWNLOADED);
        int issueFormatDateIndex = cursor.getColumnIndex(KEY_ISSUE_FORMAT_DATE);
        int headlineIndex = cursor.getColumnIndex(KEY_ISSUE_HEADLINE);
        Issue issue = new Issue();
        issue.id = cursor.getInt(issueIDIndex);
        issue.issueDate = cursor.getString(issueDateIndex);
        issue.issueUrl = cursor.getString(issueURLIndex);
        issue.coverImageUrl = cursor.getString(issueCoverImageUrlIndex);
        issue.isDownloaded = Boolean.getBoolean(cursor.getString(issueIsDownloadedIndex));
        issue.issueFormatDate = cursor.getString(issueFormatDateIndex);
        issue.headline = cursor.getString(headlineIndex);
        issue.containArticle = getArticleListByIssueDate(issue.issueDate);
        List<String> sectionList = new ArrayList<>();
        for (Article a : issue.containArticle) {
            if (!sectionList.contains(a.section)) {
                sectionList.add(a.section);
            }
        }
        issue.categorySection = sectionList;
        return issue;
    }

    public Vocabulary getVocabularyFromCursor(Cursor cursor) {
        Vocabulary vocabulary = new Vocabulary();

        int idIndex = cursor.getColumnIndex(KEY_ID);
        int vocabularyContentIndex = cursor.getColumnIndex(KEY_VOCABULARY_CONTENT);
        int collectedDateIndex = cursor.getColumnIndex(KEY_COLLECTED_DATE);
        int collectedTimeIndex = cursor.getColumnIndex(KEY_COLLECTED_TIME);
        int belongedSentenceIndex = cursor.getColumnIndex(KEY_BELONGED_SENTENCE);
        int belongedParagraphIndex = cursor.getColumnIndex(KEY_BELONGED_PARAGRAPH);
        int belongedArticleTitleIndex = cursor.getColumnIndex(KEY_BELONGED_ARTICLE_TITLE);
        int belongedSectionNameIndex = cursor.getColumnIndex(KEY_BELONGED_SECTION_NAME);
        int belongedIssueDateIndex = cursor.getColumnIndex(KEY_BELONGED_ISSUE_DATE);
        int belongedArticleUrlIndex = cursor.getColumnIndex(KEY_BELONGED_ARTICLE_URL);

        vocabulary.rowId = cursor.getInt(idIndex);
        vocabulary.vocabularyContent = cursor.getString(vocabularyContentIndex);
        vocabulary.collectedDate = cursor.getString(collectedDateIndex);
        vocabulary.collectedTime = cursor.getString(collectedTimeIndex);
        vocabulary.belongedSentence = cursor.getString(belongedSentenceIndex);
        vocabulary.belongedParagraph = cursor.getString(belongedParagraphIndex);
        vocabulary.belongedArticleTitle = cursor.getString(belongedArticleTitleIndex);
        vocabulary.belongedSectionName = cursor.getString(belongedSectionNameIndex);
        vocabulary.belongedIssueDate = cursor.getString(belongedIssueDateIndex);
        vocabulary.belongedArticleUrl = cursor.getString(belongedArticleUrlIndex);

        return vocabulary;
    }

    public Article getArticleIDFromCursor(Cursor cursor) {
        Article article = new Article();
        int idIndex = cursor.getColumnIndex(KEY_ID);
        article.rowIdInDB = cursor.getInt(idIndex);
        return article;
    }

    public Article getArticleFromCursor(Cursor cursor) {
        Article article = new Article();
        int idIndex = cursor.getColumnIndex(KEY_ID);
        int issueDateIndex = cursor.getColumnIndex(KEY_ISSUE_DATE);
        int sectionIndex = cursor.getColumnIndex(KEY_SECTION);
        int titleIndex = cursor.getColumnIndex(KEY_TITLE);
        int flytitleIndex = cursor.getColumnIndex(KEY_FLYTITLE);
        int articleUrlIndex = cursor.getColumnIndex(KEY_ARTICLE_URL);
        int audioUrlIndex = cursor.getColumnIndex(KEY_AUDIO_URL);
        int localeAudioUrlIndex = cursor.getColumnIndex(KEY_LOCALE_AUDIO_URL);
        int mainArticleImageIndex = cursor.getColumnIndex(KEY_MAIN_ARTICLE_IMAGE);
        int articleImageIndex = cursor.getColumnIndex(KEY_ARTICLE_IMAGE);
        int articleRubricIndex = cursor.getColumnIndex(KEY_ARTICLE_RUBRIC);
        int audioDurationIndex = cursor.getColumnIndex(KEY_AUDIO_DURATION);
        int isBookmarkIndex = cursor.getColumnIndex(KEY_IS_BOOKMARK);
        int issueIDIndex = cursor.getColumnIndex(KEY_ISSUE_ID);

        article.rowIdInDB = cursor.getInt(idIndex);
        article.date = cursor.getString(issueDateIndex);
        article.section = cursor.getString(sectionIndex);
        article.title = cursor.getString(titleIndex);
        article.flyTitle = cursor.getString(flytitleIndex);
        article.articleUrl = cursor.getString(articleUrlIndex);
        article.audioUrl = cursor.getString(audioUrlIndex);
        article.localeAudioUrl = cursor.getString(localeAudioUrlIndex);
        article.mainArticleImage = cursor.getString(mainArticleImageIndex);
//        article.date = cursor.getString(articleImageIndex);
        article.articleRubric = cursor.getString(articleRubricIndex);
        article.audioDuration = cursor.getFloat(audioDurationIndex);
        int bookmark = cursor.getInt(isBookmarkIndex);
        article.isBookmark = bookmark == 1;
//        article. = cursor.getString(issueIDIndex);
        article.paragraphList = queryParagraphListByArticleID(article.rowIdInDB);
        return article;
    }

    private List<Article> getArticleListByIssueDate(String issueDate) {
       sDatabase = openInchoateDB();
        // Select * from article where issue_date='' ;
        String query = "SELECT * FROM " + TABLE_NAME_ARTICLE + " WHERE "
                + KEY_ISSUE_DATE + " =\'" + issueDate + "\'";
        Log.d(TAG, "getArticleListByIssueDate: query = " + query);
        Cursor cursor = sDatabase.rawQuery(query, null);

        List<Article> articleList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Article a = getArticleFromCursor(cursor);
            articleList.add(a);
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        if (cursor != null) {
            cursor.close();
        }
        return articleList;
    }

    private List<Article> getIssueListByMonth(String issueDate) {
        Cursor cursor = queryArticleByIssueDate(issueDate);
        List<Article> articleList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Article a = getArticleFromCursor(cursor);
            articleList.add(a);
        }
        if (cursor != null) {
            cursor.close();
        }
        Log.d(TAG, "articleRowIDInDB: articleList.size() = " + articleList.size());
        return articleList;
    }

    private List<Paragraph> queryParagraphListByArticleID(int articleRowID) {
        // 如果 Paragraph 的表里查不出一个含有 articleRowID 的数据,
        // 表示这个 article 的 paragraph 没有被存入过
       sDatabase = openInchoateDB();
        // Select * from paragraph where belonged_article_id='' or paragraph_content='';
        String queryByArticleID = "SELECT * FROM " + TABLE_NAME_PARAGRAPH + " WHERE "
                + KEY_BELONGED_ARTICLE_ID + " =\'" + articleRowID + "\'";
        Cursor cursor = sDatabase.rawQuery(queryByArticleID, null);
        List<Paragraph> paragraphList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Paragraph paragraph = getParagraphFromCursor(cursor);
            paragraphList.add(paragraph);
        }
        if (sDatabase != null) {
            closeInchoateDB();
        }
        if (cursor != null) {
            cursor.close();
        }
        return paragraphList;
    }

    private Issue getNewestIssueDataFromDB() {
        List<Issue> issueList = queryAllIssue();
        long currentTime = System.currentTimeMillis();

        Issue newestIssue = null;
        int minIndex = 0;
        long minDiff = 0;
        if (issueList != null && issueList.size() == 1) {
            newestIssue = issueList.get(0);
        }
        if (issueList != null && issueList.size() > 1) {
            for (int i = 0; i < issueList.size(); i++) {
                Issue is = issueList.get(i);
                Log.d(TAG, "getNewestIssueDataFromDB: is.issueFormatDate = " + is.issueFormatDate);
                long time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                        .parse(is.issueFormatDate + " 00:00:00", new ParsePosition(0)).getTime();
                long diff = Math.abs((currentTime - time));
                if (i == 0) {
                    minDiff = diff;
                }
                if (minDiff > diff) {
                    minDiff = diff;
                    minIndex = i;
                }
            }
            newestIssue = issueList.get(minIndex);
        }

        return newestIssue;
    }

    private Paragraph getParagraphFromCursor(Cursor cursor) {
        Paragraph paragraph = new Paragraph();
        paragraph.id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
        int isEditorsNote = cursor.getInt(cursor.getColumnIndex(KEY_IS_EDITORS_NOTE));
        paragraph.isEditorsNote = isEditorsNote == 1;
        int isRelatedSuggestion = cursor.getInt(cursor.getColumnIndex(KEY_IS_RELATED_SUGGESTION));
        paragraph.isRelatedSuggestion = isRelatedSuggestion == 1;
        paragraph.paragraph = cursor.getString(cursor.getColumnIndex(KEY_PARAGRAPH_CONTENT));
        paragraph.theOrderOfParagraph = cursor.getInt(cursor.getColumnIndex(KEY_ORDER_OF_PARAGRAPH));
        paragraph.belongedArticleID = cursor.getInt(cursor.getColumnIndex(KEY_BELONGED_ARTICLE_ID));
        return paragraph;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ISSUE);
        db.execSQL(CREATE_TABLE_ARTICLE);
        db.execSQL(CREATE_TABLE_PARAGRAPH);
        db.execSQL(CREATE_TABLE_VOCABULARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static class InchoateDBErrorHandler implements DatabaseErrorHandler {
        public static final String TAG = "InchoateDBErrorHandler";

        @Override
        public void onCorruption(SQLiteDatabase dbObj) {
            Log.e(TAG, "onCorruption: corrupted path " + dbObj.getPath());
        }
    }
}
