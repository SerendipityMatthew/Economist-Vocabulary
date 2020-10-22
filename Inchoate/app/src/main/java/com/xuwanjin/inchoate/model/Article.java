package com.xuwanjin.inchoate.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.database.dao.greendao.ParagraphConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.DaoException;


@Entity
public class Article implements Parcelable, Cloneable {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    public Long rowIdInDB;
    @Property(nameInDb = "section")
    public String section;
    @Property(nameInDb = "headline")
    public String headline;
    @Property(nameInDb = "issue_date")
    public String date;
    @Property(nameInDb = "title")
    public String title;
    @Property(nameInDb = "flytitle")
    public String flyTitle;

    @Transient
    public String summary;
    @Property(nameInDb = "main_article_image")
    public String mainArticleImage;
    @Transient
    public int readTime;
    @Property(nameInDb = "audio_url")
    public String audioUrl;
    @Property(nameInDb = "locale_audio_url")
    public String localeAudioUrl;
    @Property(nameInDb = "audio_duration")
    public float audioDuration;
    @Property(nameInDb = "is_bookmark")
    public boolean isBookmark;
    @Transient
    public String content;
    @Transient
    public String imageUrl;
    @Property(nameInDb = "article_url")
    public String articleUrl;
    @Property(nameInDb = "article_rubric")
    public String articleRubric;
    @ToMany(
            referencedJoinProperty = "id"
    )
    public List<Paragraph> paragraphList;

    public Article() {

    }

    protected Article(Parcel in) {
        rowIdInDB = in.readLong();
        section = in.readString();
        headline = in.readString();
        date = in.readString();
        title = in.readString();
        flyTitle = in.readString();
        summary = in.readString();
        mainArticleImage = in.readString();
        readTime = in.readInt();
        audioUrl = in.readString();
        localeAudioUrl = in.readString();
        audioDuration = in.readFloat();
        isBookmark = in.readByte() != 0;
        content = in.readString();
        imageUrl = in.readString();
        articleUrl = in.readString();
        articleRubric = in.readString();
        paragraphList = in.createTypedArrayList(Paragraph.CREATOR);
    }

    @Generated(hash = 181580584)
    public Article(Long rowIdInDB, String section, String headline, String date,
            String title, String flyTitle, String mainArticleImage, String audioUrl,
            String localeAudioUrl, float audioDuration, boolean isBookmark,
            String articleUrl, String articleRubric) {
        this.rowIdInDB = rowIdInDB;
        this.section = section;
        this.headline = headline;
        this.date = date;
        this.title = title;
        this.flyTitle = flyTitle;
        this.mainArticleImage = mainArticleImage;
        this.audioUrl = audioUrl;
        this.localeAudioUrl = localeAudioUrl;
        this.audioDuration = audioDuration;
        this.isBookmark = isBookmark;
        this.articleUrl = articleUrl;
        this.articleRubric = articleRubric;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(rowIdInDB);
        dest.writeString(section);
        dest.writeString(headline);
        dest.writeString(date);
        dest.writeString(title);
        dest.writeString(flyTitle);
        dest.writeString(summary);
        dest.writeString(mainArticleImage);
        dest.writeInt(readTime);
        dest.writeString(audioUrl);
        dest.writeString(localeAudioUrl);
        dest.writeFloat(audioDuration);
        dest.writeByte((byte) (isBookmark ? 1 : 0));
        dest.writeString(content);
        dest.writeString(imageUrl);
        dest.writeString(articleUrl);
        dest.writeString(articleRubric);
        dest.writeTypedList(paragraphList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 434328755)
    private transient ArticleDao myDao;

    @NonNull
    @Override
    public Article clone() throws CloneNotSupportedException {
        Article article = (Article) super.clone();
        return article;
    }

    @Override
    public String toString() {
        return "Article{" +
                "rowIdInDB=" + rowIdInDB +
                ", section='" + section + '\'' +
                ", headline='" + headline + '\'' +
                ", date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", flyTitle='" + flyTitle + '\'' +
                ", summary='" + summary + '\'' +
                ", mainArticleImage='" + mainArticleImage + '\'' +
                ", readTime=" + readTime +
                ", audioUrl='" + audioUrl + '\'' +
                ", localeAudioUrl='" + localeAudioUrl + '\'' +
                ", audioDuration=" + audioDuration +
                ", isBookmark=" + isBookmark +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", articleUrl='" + articleUrl + '\'' +
                ", articleRubric='" + articleRubric + '\'' +
                ", paragraphList=" + paragraphList +
                '}';
    }

    public Long getRowIdInDB() {
        return this.rowIdInDB;
    }

    public void setRowIdInDB(Long rowIdInDB) {
        this.rowIdInDB = rowIdInDB;
    }

    public String getSection() {
        return this.section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getHeadline() {
        return this.headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFlyTitle() {
        return this.flyTitle;
    }

    public void setFlyTitle(String flyTitle) {
        this.flyTitle = flyTitle;
    }

    public String getMainArticleImage() {
        return this.mainArticleImage;
    }

    public void setMainArticleImage(String mainArticleImage) {
        this.mainArticleImage = mainArticleImage;
    }

    public String getAudioUrl() {
        return this.audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getLocaleAudioUrl() {
        return this.localeAudioUrl;
    }

    public void setLocaleAudioUrl(String localeAudioUrl) {
        this.localeAudioUrl = localeAudioUrl;
    }

    public float getAudioDuration() {
        return this.audioDuration;
    }

    public void setAudioDuration(float audioDuration) {
        this.audioDuration = audioDuration;
    }

    public boolean getIsBookmark() {
        return this.isBookmark;
    }

    public void setIsBookmark(boolean isBookmark) {
        this.isBookmark = isBookmark;
    }

    public String getArticleUrl() {
        return this.articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleRubric() {
        return this.articleRubric;
    }

    public void setArticleRubric(String articleRubric) {
        this.articleRubric = articleRubric;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 802141822)
    public List<Paragraph> getParagraphList() {
        if (paragraphList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ParagraphDao targetDao = daoSession.getParagraphDao();
            List<Paragraph> paragraphListNew = targetDao
                    ._queryArticle_ParagraphList(rowIdInDB);
            synchronized (this) {
                if (paragraphList == null) {
                    paragraphList = paragraphListNew;
                }
            }
        }
        return paragraphList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1509649389)
    public synchronized void resetParagraphList() {
        paragraphList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2112142041)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getArticleDao() : null;
    }

}
