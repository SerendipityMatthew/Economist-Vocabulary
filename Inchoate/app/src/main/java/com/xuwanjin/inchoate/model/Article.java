package com.xuwanjin.inchoate.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Article implements Parcelable {
    public String section;
    public String headline;
    public String date;
    public String title;
    public String flyTitle;
    public String summary;
    public String mainArticleImage;
    public int readTime;
    public String audioUrl;
    public float audioDuration;
    public boolean isBookmark;
    public String content;
    public String imageUrl;
    public String articleUrl;
    public List<Paragraph> paragraphList;

    public Article(){

    }
    protected Article(Parcel in) {
        section = in.readString();
        headline = in.readString();
        date = in.readString();
        title = in.readString();
        flyTitle = in.readString();
        summary = in.readString();
        mainArticleImage = in.readString();
        readTime = in.readInt();
        audioUrl = in.readString();
        audioDuration = in.readFloat();
        isBookmark = in.readByte() != 0;
        content = in.readString();
        imageUrl = in.readString();
        articleUrl = in.readString();
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

    @Override
    public String toString() {
        return "Article{" +
                "section='" + section + '\'' +
                ", headline='" + headline + '\'' +
                ", date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", flyTitle='" + flyTitle + '\'' +
                ", summary='" + summary + '\'' +
                ", mainArticleImage='" + mainArticleImage + '\'' +
                ", readTime=" + readTime +
                ", audioUrl='" + audioUrl + '\'' +
                ", audioDuration=" + audioDuration +
                ", isBookmark=" + isBookmark +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", articleUrl='" + articleUrl + '\'' +
                ", paragraphList=" + paragraphList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(section);
        dest.writeString(headline);
        dest.writeString(date);
        dest.writeString(title);
        dest.writeString(flyTitle);
        dest.writeString(summary);
        dest.writeString(mainArticleImage);
        dest.writeInt(readTime);
        dest.writeString(audioUrl);
        dest.writeFloat(audioDuration);
        dest.writeByte((byte) (isBookmark ? 1 : 0));
        dest.writeString(content);
        dest.writeString(imageUrl);
        dest.writeString(articleUrl);
    }
}
