package me.xuwanjin.economistvocabulary.model;

import java.util.ArrayList;

public class Article {
    private String date;
    private ArrayList<String> vocabularyList;
    private String title;
    private String subHeadline;
    private String content;
    private String issueDate;
    private String belongedColumn;
    private String articleUrl;
    private ArrayList<String> imagesUrls;
    private String audioFileUrl;
    private String description;
    private String audioUrl;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getVocabularyList() {
        return vocabularyList;
    }

    public void setVocabularyList(ArrayList<String> vocabularyList) {
        this.vocabularyList = vocabularyList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubHeadline() {
        return subHeadline;
    }

    public void setSubHeadline(String subHeadline) {
        this.subHeadline = subHeadline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getBelongedColumn() {
        return belongedColumn;
    }

    public void setBelongedColumn(String belongedColumn) {
        this.belongedColumn = belongedColumn;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public ArrayList<String> getImagesUrls() {
        return imagesUrls;
    }

    public void setImagesUrls(ArrayList<String> imagesUrls) {
        this.imagesUrls = imagesUrls;
    }

    public String getAudioFileUrl() {
        return audioFileUrl;
    }

    public void setAudioFileUrl(String audioFileUrl) {
        this.audioFileUrl = audioFileUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    @Override
    public String toString() {
        return "Article{" +
                "date='" + date + '\'' +
                ", vocabularyList=" + vocabularyList +
                ", title='" + title + '\'' +
                ", subHeadline='" + subHeadline + '\'' +
                ", content='" + content + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", belongedColumn='" + belongedColumn + '\'' +
                ", articleUrl='" + articleUrl + '\'' +
                ", imagesUrls='" + imagesUrls + '\'' +
                ", audioFileUrl='" + audioFileUrl + '\'' +
                ", description='" + description + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                '}';
    }
}
