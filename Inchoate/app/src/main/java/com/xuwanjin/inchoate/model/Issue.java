package com.xuwanjin.inchoate.model;

import java.util.List;

public class Issue {
    public String issueDate;
    public boolean isDownloaded;
    public String coverImageUrl;
    public String headline;
    public String issueUrl;
    public List<Article> containArticle;

    @Override
    public String toString() {
        return "Issue{" +
                "issueDate='" + issueDate + '\'' +
                ", isDownloaded=" + isDownloaded +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", headline='" + headline + '\'' +
                ", issueUrl='" + issueUrl + '\'' +
                ", containArticle=" + containArticle +
                '}';
    }
}
