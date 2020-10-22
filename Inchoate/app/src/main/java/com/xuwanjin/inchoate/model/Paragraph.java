package com.xuwanjin.inchoate.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.xuwanjin.inchoate.database.dao.greendao.CharSequenceConverter;
import com.xuwanjin.inchoate.database.dao.greendao.ParagraphConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Paragraph implements Parcelable {
    // 存储到数据库里的时候偶, 需要保证取出来的能拼凑按照顺序的段落
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    public Long id;
    @Property(nameInDb = "order_of_paragraph")
    public int theOrderOfParagraph;
    public String articleName;
    @Property(nameInDb = "paragraph_content")
    public String paragraphContent;

    @Transient
    public CharSequence paragraph;
    @Property(nameInDb = "is_editors_note")
    public boolean isEditorsNote;
    @Property(nameInDb = "is_related_suggestion")
    public boolean isRelatedSuggestion;
    @Property(nameInDb = "belonged_article_id")
    public int belongedArticleID;
    public String issueDate;
    public String belongedSection;

    public Paragraph() {

    }

    protected Paragraph(Parcel in) {
        articleName = in.readString();
        paragraphContent = in.readString();
        isEditorsNote = Boolean.parseBoolean(in.readString());
        isRelatedSuggestion = Boolean.parseBoolean(in.readString());
        belongedArticleID = in.readInt();
        theOrderOfParagraph = in.readInt();
        id = in.readLong();
    }

    @Generated(hash = 720031931)
    public Paragraph(Long id, int theOrderOfParagraph, String articleName,
            String paragraphContent, boolean isEditorsNote,
            boolean isRelatedSuggestion, int belongedArticleID, String issueDate,
            String belongedSection) {
        this.id = id;
        this.theOrderOfParagraph = theOrderOfParagraph;
        this.articleName = articleName;
        this.paragraphContent = paragraphContent;
        this.isEditorsNote = isEditorsNote;
        this.isRelatedSuggestion = isRelatedSuggestion;
        this.belongedArticleID = belongedArticleID;
        this.issueDate = issueDate;
        this.belongedSection = belongedSection;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(articleName);
        dest.writeString(paragraphContent.toString());
        dest.writeString(String.valueOf(isEditorsNote));
        dest.writeString(String.valueOf(isRelatedSuggestion));
        dest.writeInt(belongedArticleID);
        dest.writeInt(theOrderOfParagraph);
        dest.writeLong(id);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTheOrderOfParagraph() {
        return this.theOrderOfParagraph;
    }

    public void setTheOrderOfParagraph(int theOrderOfParagraph) {
        this.theOrderOfParagraph = theOrderOfParagraph;
    }

    public String getArticleName() {
        return this.articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getParagraphContent() {
        return this.paragraphContent;
    }

    public void setParagraphContent(String paragraphContent) {
        this.paragraphContent = paragraphContent;
    }

    public boolean getIsEditorsNote() {
        return this.isEditorsNote;
    }

    public void setIsEditorsNote(boolean isEditorsNote) {
        this.isEditorsNote = isEditorsNote;
    }

    public boolean getIsRelatedSuggestion() {
        return this.isRelatedSuggestion;
    }

    public void setIsRelatedSuggestion(boolean isRelatedSuggestion) {
        this.isRelatedSuggestion = isRelatedSuggestion;
    }

    public int getBelongedArticleID() {
        return this.belongedArticleID;
    }

    public void setBelongedArticleID(int belongedArticleID) {
        this.belongedArticleID = belongedArticleID;
    }

    public String getIssueDate() {
        return this.issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getBelongedSection() {
        return this.belongedSection;
    }

    public void setBelongedSection(String belongedSection) {
        this.belongedSection = belongedSection;
    }

}
