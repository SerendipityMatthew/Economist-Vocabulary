package com.xuwanjin.inchoate.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.database.dao.greendao.ArticleConverter;
import com.xuwanjin.inchoate.database.dao.greendao.CategorySectionConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.DaoException;

@Entity
public class Issue implements Parcelable, Cloneable {
    public String issueDate;
    @Property(nameInDb = "issue_format_date")
    public String issueFormatDate;
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    public Long id;
    @Property(nameInDb = "is_downloaded")
    public boolean isDownloaded;
    @Property(nameInDb = "cover_image_url")
    public String coverImageUrl;
    @Property(nameInDb = "issue_headline")
    public String headline;
    @Property(nameInDb = "issue_url")
    public String issueUrl;
    @Property(nameInDb = "url_id")
    public String urlID;
    @ToMany(
            referencedJoinProperty = "rowIdInDB"
    )
    public List<Article> containArticle;
    @Transient
    public List<String> categorySection;


    public Issue() {

    }

    protected Issue(Parcel in) {
        issueDate = in.readString();
        issueFormatDate = in.readString();
        id = in.readLong();
        isDownloaded = in.readByte() != 0;
        coverImageUrl = in.readString();
        headline = in.readString();
        issueUrl = in.readString();
        containArticle = in.createTypedArrayList(Article.CREATOR);
        categorySection = in.createStringArrayList();
    }

    @Generated(hash = 822824836)
    public Issue(String issueDate, String issueFormatDate, Long id,
            boolean isDownloaded, String coverImageUrl, String headline,
            String issueUrl, String urlID) {
        this.issueDate = issueDate;
        this.issueFormatDate = issueFormatDate;
        this.id = id;
        this.isDownloaded = isDownloaded;
        this.coverImageUrl = coverImageUrl;
        this.headline = headline;
        this.issueUrl = issueUrl;
        this.urlID = urlID;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(issueDate);
        dest.writeString(issueFormatDate);
        dest.writeLong(id);
        dest.writeByte((byte) (isDownloaded ? 1 : 0));
        dest.writeString(coverImageUrl);
        dest.writeString(headline);
        dest.writeString(issueUrl);
        dest.writeTypedList(containArticle);
        dest.writeStringList(categorySection);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 724440415)
    private transient IssueDao myDao;

    @Override
    public String toString() {
        return "Issue{" +
                "issueDate='" + issueDate + '\'' +
                ", issueFormatDate='" + issueFormatDate + '\'' +
                ", id=" + id +
                ", isDownloaded=" + isDownloaded +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", headline='" + headline + '\'' +
                ", issueUrl='" + issueUrl + '\'' +
                ", containArticle=" + containArticle +
                ", categorySection=" + categorySection +
                '}';
    }

    @NonNull
    @Override
    public Issue clone() throws CloneNotSupportedException {
        Issue issue = (Issue) super.clone();
        return issue;
    }

    public String getIssueDate() {
        return this.issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueFormatDate() {
        return this.issueFormatDate;
    }

    public void setIssueFormatDate(String issueFormatDate) {
        this.issueFormatDate = issueFormatDate;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsDownloaded() {
        return this.isDownloaded;
    }

    public void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getHeadline() {
        return this.headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getIssueUrl() {
        return this.issueUrl;
    }

    public void setIssueUrl(String issueUrl) {
        this.issueUrl = issueUrl;
    }

    public String getUrlID() {
        return this.urlID;
    }

    public void setUrlID(String urlID) {
        this.urlID = urlID;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 991244128)
    public List<Article> getContainArticle() {
        if (containArticle == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ArticleDao targetDao = daoSession.getArticleDao();
            List<Article> containArticleNew = targetDao
                    ._queryIssue_ContainArticle(id);
            synchronized (this) {
                if (containArticle == null) {
                    containArticle = containArticleNew;
                }
            }
        }
        return containArticle;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 231894905)
    public synchronized void resetContainArticle() {
        containArticle = null;
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
    @Generated(hash = 884668014)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getIssueDao() : null;
    }

}
