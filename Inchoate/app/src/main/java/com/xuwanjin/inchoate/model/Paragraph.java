package com.xuwanjin.inchoate.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class Paragraph implements Parcelable {
    // 存储到数据库里的时候偶, 需要保证取出来的能拼凑按照顺序的段落
    public int id;
    public int theOrderOfParagraph;
    public String articleName;
    public CharSequence paragraph;
    public boolean isEditorsNote;
    public boolean isRelatedSuggestion;
    public int belongedArticleID;

    public Paragraph() {

    }

    protected Paragraph(Parcel in) {
        articleName = in.readString();
        paragraph = in.readString();
        isEditorsNote = Boolean.parseBoolean(in.readString());
        isRelatedSuggestion = Boolean.parseBoolean(in.readString());
        belongedArticleID = in.readInt();
        theOrderOfParagraph = in.readInt();
        id = in.readInt();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(articleName);
        dest.writeString(paragraph.toString());
        dest.writeString(String.valueOf(isEditorsNote));
        dest.writeString(String.valueOf(isRelatedSuggestion));
        dest.writeInt(belongedArticleID);
        dest.writeInt(theOrderOfParagraph);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Paragraph> CREATOR = new Creator<Paragraph>() {
        @Override
        public Paragraph createFromParcel(Parcel in) {
            return new Paragraph(in);
        }

        @Override
        public Paragraph[] newArray(int size) {
            return new Paragraph[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTheOrderOfParagraph() {
        return theOrderOfParagraph;
    }

    public void setTheOrderOfParagraph(int theOrderOfParagraph) {
        this.theOrderOfParagraph = theOrderOfParagraph;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public CharSequence getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public boolean isEditorsNote() {
        return isEditorsNote;
    }

    public void setEditorsNote(boolean editorsNote) {
        isEditorsNote = editorsNote;
    }

    public boolean isRelatedSuggestion() {
        return isRelatedSuggestion;
    }

    public void setRelatedSuggestion(boolean relatedSuggestion) {
        isRelatedSuggestion = relatedSuggestion;
    }

    public int getBelongedArticleID() {
        return belongedArticleID;
    }

    public void setBelongedArticleID(int belongedArticleID) {
        this.belongedArticleID = belongedArticleID;
    }

    public static Creator<Paragraph> getCREATOR() {
        return CREATOR;
    }
}
