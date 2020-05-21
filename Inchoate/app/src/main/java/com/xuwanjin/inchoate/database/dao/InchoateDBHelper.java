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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class InchoateDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "InchoateDBHelper";
    public static final String DATABASE_NAME = "inchoate.db";
    static final String TABLE_NAME_ISSUE = "issue";
    static final String TABLE_NAME_ARTICLE = "article";
    static final String TABLE_NAME_PARAGRAPH = "paragraph";
    public Context mContext;
    private static volatile SQLiteDatabase sDatabase;

    private static final long RECORD_NOT_EXISTED_IN_DB = -1000;
    private static final String KEY_ID = "id";

    // issue of table
    private static final String KEY_COVER_IMAGE_URL = "cover_image_url";
    private static final String KEY_IS_DOWNLOADED = "is_downloaded";
    private static final String KEY_ISSUE_URL = "issue_url";
    private static final String KEY_ISSUE_FORMAT_DATE = "issue_format_date";
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


    // paragraph of table
    private static final String KEY_IS_EDITORS_NOTE = "is_editors_note";
    private static final String KEY_IS_RELATED_SUGGESTION = "is_related_suggestion";
    private static final String KEY_PARAGRAPH_CONTENT = "paragraph_content";
    private static final String KEY_ORDER_OF_PARAGRAPH = "order_of_paragraph";
    private static final String KEY_BELONGED_ARTICLE_ID = "belonged_article_id";


    private static final String KEY_ID_PARA = KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,";

    private static final String CREATE_TABLE_ISSUE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_ISSUE + " ( " + KEY_ID_PARA
            + KEY_ISSUE_DATE + " TEXT,"
            + KEY_COVER_IMAGE_URL + " TEXT,"
            + KEY_IS_DOWNLOADED + " TEXT,"
            + KEY_ISSUE_URL + " TEXT,"
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

    // 创建索引
    private static final String CREATE_TABLE_ISSUE_INDEX = "CREATE TABLE "
            + TABLE_NAME_ISSUE + " (" + " ON ";

    public synchronized InchoateDBHelper open() {
        if (sDatabase == null || !sDatabase.isOpen() || !sDatabase.isReadOnly()) {
            sDatabase = openInchoateDB();
        }
        return this;
    }

    private SQLiteDatabase openInchoateDB() {
        SQLiteDatabase database;
        database = getWritableDatabase();
        return database;
    }

    public InchoateDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, 100);
        this.mContext = context;
    }

    public InchoateDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, version, errorHandler);
        this.mContext = context;
    }

    public void setBookmarkStatus(boolean isBookmark) {
        sDatabase = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_IS_BOOKMARK, isBookmark);
        sDatabase.update(TABLE_NAME_ARTICLE, contentValues, KEY_ID + "=?", new String[]{});
        sDatabase.close();
    }

    public Cursor getBookmarkedArticle() {
        sDatabase = openInchoateDB();
        // Select * from article where is_bookmark='true';
        String query = "SELECT * FROM " + TABLE_NAME_ARTICLE + " WHERE " + KEY_IS_BOOKMARK + " =\'true\'";
        Cursor cursor = sDatabase.rawQuery(query, null);
//        sDatabase.close();
        return cursor;
    }

    public List<Issue> queryIssueByIssueDate(String issueDate) {
        List<Issue> issueList = new ArrayList<>();
        sDatabase = openInchoateDB();
        // Select * from article where issue_date='';
        String query = "SELECT * FROM " + TABLE_NAME_ISSUE + " WHERE " + KEY_ISSUE_DATE + " =\'" + issueDate + "\'";
        Cursor cursor = sDatabase.rawQuery(query, null);
        while (cursor != null && cursor.moveToNext()) {
            Issue issue = new Issue();
            issue = getIssueFromCursor(cursor);
            issueList.add(issue);
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

    public Cursor queryArticleByIssueDateSectionTitle(Article article, String issueDate) {
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
    public List<Paragraph> queryParagraphByContentAndArticleID(String content, long articleRowID) {
        // 如果 Paragraph 的表里查不出一个含有 articleRowID 的数据,
        // 表示这个 article 的 paragraph 没有被存入过
        sDatabase = openInchoateDB();
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
        if (cursor != null) {
            cursor.close();
        }
        return paragraphList;
    }

    public long insertIssueData(Issue issue) {
        SQLiteDatabase database = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_COVER_IMAGE_URL, issue.coverImageUrl);
        contentValues.put(KEY_IS_DOWNLOADED, issue.isDownloaded);
        contentValues.put(KEY_ISSUE_URL, issue.issueUrl);
        contentValues.put(KEY_ISSUE_DATE, issue.issueDate);
        contentValues.put(KEY_ISSUE_FORMAT_DATE, issue.issueFormatDate);
        long rowID = database.insert(TABLE_NAME_ISSUE, null, contentValues);
        if (database != null) {
            database.close();
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
        SQLiteDatabase database = openInchoateDB();
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
        long rowID = database.insert(TABLE_NAME_ARTICLE, null, contentValues);
        if (database != null) {
            database.close();
        }
        return rowID;
    }

    public long updateArticleAudioLocaleUrl(Article article, String issueDate) {
        if (!isArticleExistedInDB(article, issueDate)) {
            Log.d(TAG, "updateArticleAudioLocaleUrl: ");
            return RECORD_NOT_EXISTED_IN_DB;
        }
        SQLiteDatabase database = openInchoateDB();
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
        Log.d(TAG, "updateArticleAudioLocaleUrl: localeAudioUrl = " + article.localeAudioUrl);
        Log.d(TAG, "updateArticleAudioLocaleUrl: article.rowIdInDB = " + article.rowIdInDB);
        long rowID = database.update(TABLE_NAME_ARTICLE, contentValues, whereClause, new String[]{String.valueOf(article.rowIdInDB)});
        Log.d(TAG, "updateArticleAudioLocaleUrl: rowID = " + rowID);
        if (database != null) {
            database.close();
        }
        return rowID;
    }

    public long insertParagraphData(Paragraph paragraph, long articleRowID) {
        SQLiteDatabase database = openInchoateDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_IS_EDITORS_NOTE, paragraph.isEditorsNote);
        contentValues.put(KEY_IS_RELATED_SUGGESTION, paragraph.isRelatedSuggestion);
        contentValues.put(KEY_PARAGRAPH_CONTENT, paragraph.paragraph);
        contentValues.put(KEY_ORDER_OF_PARAGRAPH, paragraph.theOrderOfParagraph);
        contentValues.put(KEY_BELONGED_ARTICLE_ID, articleRowID);
        long rowID = database.insert(TABLE_NAME_PARAGRAPH, null, contentValues);
        if (database != null) {
            database.close();
        }
        return rowID;
    }

    // 查看是否存在, 如果存在,                获取 rowID
    //              如果不存在. 则插入数据,   并且返回 rowID;
    public void insertWholeData(final Issue issue) {
        Log.d(TAG, "insertWholeData: ");
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
        for (Article article : issue.containArticle) {
            long id = articleRowIDInDB(article, issue.issueDate);
            long articleRowID = RECORD_NOT_EXISTED_IN_DB;
            if (id == RECORD_NOT_EXISTED_IN_DB) {
                articleRowID = insertArticleData(article, issueRowID, issue.issueDate);
            } else {
                articleRowID = id;
            }
            for (Paragraph paragraph : article.paragraphList) {
                long paragraphID = paragraphRowIDInDB(paragraph.paragraph, articleRowID);
                long paragraphRowID = RECORD_NOT_EXISTED_IN_DB;
                if (paragraphID == RECORD_NOT_EXISTED_IN_DB) {
                    paragraphRowID = insertParagraphData(paragraph, articleRowID);
                } else {
                    paragraphRowID = paragraphID;
                }
            }
        }
    }

    private boolean isArticleExistedInDB(Article article, String issueDate) {
        long rowID = articleRowIDInDB(article, issueDate);
        Log.d(TAG, "isArticleExistedInDB: rowID = " + rowID);
        if (rowID == RECORD_NOT_EXISTED_IN_DB) {
            return false;
        }
        return true;
    }


    private long articleRowIDInDB(Article article, String issueDate) {
        Cursor cursor = queryArticleByIssueDateSectionTitle(article, issueDate);
        List<Article> articleList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Article a = getArticleFromCursor(cursor);
            articleList.add(a);
        }
        Log.d(TAG, "articleRowIDInDB: articleList.size() = " + articleList.size());
        if (articleList == null || articleList.size() == 0) {
            return RECORD_NOT_EXISTED_IN_DB;
        }
        Log.d(TAG, "articleRowIDInDB: articleList.get(0) = " + articleList.get(0));
        if (articleList.size() == 1) {
            return articleList.get(0).rowIdInDB;
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
        Issue issue = new Issue();
        issue.id = cursor.getInt(issueIDIndex);
        issue.issueDate = cursor.getString(issueDateIndex);
        issue.issueUrl = cursor.getString(issueURLIndex);
        issue.coverImageUrl = cursor.getString(issueCoverImageUrlIndex);
        issue.isDownloaded = Boolean.getBoolean(cursor.getString(issueIsDownloadedIndex));
        issue.issueFormatDate = cursor.getString(issueFormatDateIndex);
        issue.containArticle = getArticleListByIssueDate(issue.issueDate);
        return issue;
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
        Cursor cursor = queryArticleByIssueDate(issueDate);
        List<Article> articleList = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Article a = getArticleFromCursor(cursor);
            articleList.add(a);
        }
        if (cursor != null){
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
        if (cursor != null) {
            cursor.close();
        }
        return paragraphList;
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
