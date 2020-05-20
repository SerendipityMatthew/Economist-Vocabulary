package com.xuwanjin.inchoate.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Issue implements Parcelable {
    public String issueDate;
    public int id;
    public boolean isDownloaded;
    public String coverImageUrl;
    public String headline;
    public String issueUrl;
    public List<Article> containArticle;
    public List<String> categorySection;

    public Issue() {

    }

    protected Issue(Parcel in) {
        issueDate = in.readString();
        isDownloaded = in.readByte() != 0;
        coverImageUrl = in.readString();
        headline = in.readString();
        issueUrl = in.readString();
        id = in.readInt();
        containArticle = in.createTypedArrayList(Article.CREATOR);
        categorySection = in.createStringArrayList();
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

    @Override
    public String toString() {
        return "Issue{" +
                "issueDate='" + issueDate + '\'' +
                ", id=" + id +
                ", isDownloaded=" + isDownloaded +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", headline='" + headline + '\'' +
                ", issueUrl='" + issueUrl + '\'' +
                ", containArticle=" + containArticle +
                ", categorySection=" + categorySection +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(issueDate);
        dest.writeByte((byte) (isDownloaded ? 1 : 0));
        dest.writeString(coverImageUrl);
        dest.writeString(headline);
        dest.writeString(issueUrl);
        dest.writeTypedList(containArticle);
        dest.writeStringList(categorySection);
        dest.writeInt(id);
    }
}
