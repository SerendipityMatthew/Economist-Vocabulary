package com.xuwanjin.inchoate.model;

import java.util.List;

public class Article {
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
}
