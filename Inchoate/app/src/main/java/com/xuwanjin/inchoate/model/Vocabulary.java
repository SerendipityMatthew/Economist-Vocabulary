package com.xuwanjin.inchoate.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Vocabulary implements Parcelable {
    public int rowId;
    public String vocabularyContent;
    public String collectedDate;
    public String collectedTime;
    public String belongedSentence;
    public String belongedParagraph;
    public String belongedArticleTitle;
    public String belongedSectionName;
    public String belongedIssueDate;
    public String belongedArticleUrl;

    /*
        Vocabulary   collectedDate ,  collectedTime,   sentence,  paragraph,  article title, issue date,    section name,    belongedArticleUrl
        Inchoate,      2020-04-24,       05:45,          xxxx,    xxxxx,       is China Win?,  2020-04-24,  China/Briefing    https://

     */
    public Vocabulary(){

    }

    protected Vocabulary(Parcel in) {
        rowId = in.readInt();
        vocabularyContent = in.readString();
        collectedDate = in.readString();
        collectedTime = in.readString();
        belongedSentence = in.readString();
        belongedParagraph = in.readString();
        belongedArticleTitle = in.readString();
        belongedSectionName = in.readString();
        belongedIssueDate = in.readString();
        belongedArticleUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rowId);
        dest.writeString(vocabularyContent);
        dest.writeString(collectedDate);
        dest.writeString(collectedTime);
        dest.writeString(belongedSentence);
        dest.writeString(belongedParagraph);
        dest.writeString(belongedArticleTitle);
        dest.writeString(belongedSectionName);
        dest.writeString(belongedIssueDate);
        dest.writeString(belongedArticleUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vocabulary> CREATOR = new Creator<Vocabulary>() {
        @Override
        public Vocabulary createFromParcel(Parcel in) {
            return new Vocabulary(in);
        }

        @Override
        public Vocabulary[] newArray(int size) {
            return new Vocabulary[size];
        }
    };

    @Override
    public String toString() {
        return "Vocabulary{" +
                "rowId=" + rowId +
                ", vocabularyContent='" + vocabularyContent + '\'' +
                ", collectedDate='" + collectedDate + '\'' +
                ", collectedTime='" + collectedTime + '\'' +
                ", belongedSentence='" + belongedSentence + '\'' +
                ", belongedParagraph='" + belongedParagraph + '\'' +
                ", belongedArticleTitle='" + belongedArticleTitle + '\'' +
                ", belongedSectionName='" + belongedSectionName + '\'' +
                ", belongedIssueDate='" + belongedIssueDate + '\'' +
                ", belongedArticleUrl='" + belongedArticleUrl + '\'' +
                '}';
    }
}
