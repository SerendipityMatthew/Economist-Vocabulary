package com.xuwanjin.inchoate.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Vocabulary implements Parcelable {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    public Long rowId;
    @Property(nameInDb = "vocabulary_content")
    public String vocabularyContent;
    @Property(nameInDb = "collected_date")
    public String collectedDate;
    @Property(nameInDb = "collected_time")
    public String collectedTime;
    @Property(nameInDb = "belonged_sentence")
    public String belongedSentence;
    @Property(nameInDb = "belonged_paragraph")
    public String belongedParagraph;
    @Property(nameInDb = "belonged_article_title")
    public String belongedArticleTitle;
    @Property(nameInDb = "belonged_section_name")
    public String belongedSectionName;
    @Property(nameInDb = "belonged_issue_date")
    public String belongedIssueDate;
    @Property(nameInDb = "belonged_article_url")
    public String belongedArticleUrl;

    /*
        Vocabulary   collectedDate ,  collectedTime,   sentence,  paragraph,  article title, issue date,    section name,    belongedArticleUrl
        Inchoate,      2020-04-24,       05:45,          xxxx,    xxxxx,       is China Win?,  2020-04-24,  China/Briefing    https://

     */
    public Vocabulary(){

    }

    protected Vocabulary(Parcel in) {
        rowId = in.readLong();
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

    @Generated(hash = 1592811324)
    public Vocabulary(Long rowId, String vocabularyContent, String collectedDate, String collectedTime, String belongedSentence,
            String belongedParagraph, String belongedArticleTitle, String belongedSectionName, String belongedIssueDate,
            String belongedArticleUrl) {
        this.rowId = rowId;
        this.vocabularyContent = vocabularyContent;
        this.collectedDate = collectedDate;
        this.collectedTime = collectedTime;
        this.belongedSentence = belongedSentence;
        this.belongedParagraph = belongedParagraph;
        this.belongedArticleTitle = belongedArticleTitle;
        this.belongedSectionName = belongedSectionName;
        this.belongedIssueDate = belongedIssueDate;
        this.belongedArticleUrl = belongedArticleUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(rowId);
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

    public Long getRowId() {
        return this.rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public String getVocabularyContent() {
        return this.vocabularyContent;
    }

    public void setVocabularyContent(String vocabularyContent) {
        this.vocabularyContent = vocabularyContent;
    }

    public String getCollectedDate() {
        return this.collectedDate;
    }

    public void setCollectedDate(String collectedDate) {
        this.collectedDate = collectedDate;
    }

    public String getCollectedTime() {
        return this.collectedTime;
    }

    public void setCollectedTime(String collectedTime) {
        this.collectedTime = collectedTime;
    }

    public String getBelongedSentence() {
        return this.belongedSentence;
    }

    public void setBelongedSentence(String belongedSentence) {
        this.belongedSentence = belongedSentence;
    }

    public String getBelongedParagraph() {
        return this.belongedParagraph;
    }

    public void setBelongedParagraph(String belongedParagraph) {
        this.belongedParagraph = belongedParagraph;
    }

    public String getBelongedArticleTitle() {
        return this.belongedArticleTitle;
    }

    public void setBelongedArticleTitle(String belongedArticleTitle) {
        this.belongedArticleTitle = belongedArticleTitle;
    }

    public String getBelongedSectionName() {
        return this.belongedSectionName;
    }

    public void setBelongedSectionName(String belongedSectionName) {
        this.belongedSectionName = belongedSectionName;
    }

    public String getBelongedIssueDate() {
        return this.belongedIssueDate;
    }

    public void setBelongedIssueDate(String belongedIssueDate) {
        this.belongedIssueDate = belongedIssueDate;
    }

    public String getBelongedArticleUrl() {
        return this.belongedArticleUrl;
    }

    public void setBelongedArticleUrl(String belongedArticleUrl) {
        this.belongedArticleUrl = belongedArticleUrl;
    }
}
