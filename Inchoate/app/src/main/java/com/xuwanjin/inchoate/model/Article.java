package com.xuwanjin.inchoate.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Article implements Parcelable, Cloneable {
    public int rowIdInDB;
    public String section;
    public String headline;
    public String date;
    public String title;
    public String flyTitle;
    public String summary;
    public String mainArticleImage;
    public int readTime;
    public String audioUrl;
    public String localeAudioUrl;
    public float audioDuration;
    public boolean isBookmark;
    public String content;
    public String imageUrl;
    public String articleUrl;
    public String articleRubric;
    public List<Paragraph> paragraphList;

    public Article() {

    }

    protected Article(Parcel in) {
        rowIdInDB = in.readInt();
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rowIdInDB);
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
}
