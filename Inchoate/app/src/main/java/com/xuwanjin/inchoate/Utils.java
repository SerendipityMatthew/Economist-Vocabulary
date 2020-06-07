package com.xuwanjin.inchoate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.navigation.NavController;

import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Internal;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.model.archive.Archive;
import com.xuwanjin.inchoate.model.archive.CoverContent;
import com.xuwanjin.inchoate.model.archive.Part;
import com.xuwanjin.inchoate.model.today.TodayFirstParts;
import com.xuwanjin.inchoate.model.today.TodayJson;
import com.xuwanjin.inchoate.model.today.TodaySecondParts;
import com.xuwanjin.inchoate.model.week.WeekJson;
import com.xuwanjin.inchoate.model.week.WeekPart;
import com.xuwanjin.inchoate.model.week.WeekSection;
import com.xuwanjin.inchoate.model.week.WeekText;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.xuwanjin.inchoate.InchoateApp.sArticleLinkedHashMap;


public class Utils {
    public static final String TAG = "Utils";

    public static void navigationController(NavController navController, int resId) {
        if (navController != null) {
            navController.navigate(resId);
        }
    }

    public static List<Article> getWholeArticle(WeekJson weekJson) {
        List<Article> allArticleList = new ArrayList<>();
        List<WeekPart> weekPartList = weekJson.data.section.hasPart.parts;

        //1.  weekPartList 是获取了一周的所有文章的json 数据
        for (WeekPart weekPart : weekPartList) {
            // weekPartList.get(0).text  获取 text 部分的 json 数据
//            List<WeekText> weekTextList = weekPartList.get(0).text;
//            weekTextList.get(0)  获取第一层数据

//          weekText.type == p 的表示段落
//          WeekText weekText = weekTextList.get(0);  // 获取的
            //2. weekPart.text 获取的是一篇文章的所有的 json 数据
            String audioUrl = "";
            float audioDuration = 0;
            String imageUrl = "";
            if (weekPart.audio.main != null) {
                audioUrl = weekPart.audio.main.url.canonical;
                audioDuration = weekPart.audio.main.duration;
            }
            if (weekPart.image != null && weekPart.image.main != null) {
                imageUrl = weekPart.image.main.url.canonical;
            }
            String articleSection = weekPart.print.section.sectionName;
            String articleUrl = weekPart.url.canonical;

            Article article = new Article();
            article.audioUrl = audioUrl;
            article.audioDuration = audioDuration;
            article.imageUrl = imageUrl;
            article.section = articleSection;
            article.title = weekPart.print.title;
            article.flyTitle = weekPart.print.flyTitle;
            article.articleRubric = weekPart.print.rubric;
            article.articleUrl = articleUrl;
            article.mainArticleImage = imageUrl;
            article.date = Utils.digitalDateSwitchToEnglishFormat(weekPart.published.substring(0, 10));
            article.paragraphList = new ArrayList<>();
            StringBuilder articleBuilder = new StringBuilder();
            int theOrderOfParagraph = 0;
            for (WeekText wholeArticleText : weekPart.text) {
                // weekText0.children 第一个 children列表里 data 字段, 合并成一个段落
                StringBuilder paragraphBuilder = new StringBuilder();
                // 3. 获取段落的所有数据
                List<WeekText> childrenText = wholeArticleText.children;
                if (childrenText == null || childrenText.size() == 0) {
                    continue;
                }
                for (WeekText paragraphText : childrenText) {
                    if (paragraphText.children != null) {
                        List<WeekText> children = paragraphText.children;
                        for (WeekText w : children) {
                            paragraphBuilder.append(w.data);
                        }
                    } else {
                        if (paragraphText.type.equals("text")) {
                            paragraphBuilder.append(paragraphText.data);
                        }
                    }
                }
                Paragraph paragraph = filteredParagraph(paragraphBuilder);

                if (paragraph != null) {
                    theOrderOfParagraph++;
                    paragraph.articleName = article.title;
                    paragraph.theOrderOfParagraph = theOrderOfParagraph;
                    article.paragraphList.add(paragraph);
                }
                articleBuilder.append(paragraphBuilder);
            }
            article.content = articleBuilder.toString();
            allArticleList.add(article);
        }
        return allArticleList;
    }

    public static Paragraph filteredParagraph(StringBuilder paragraphBuilder) {
        Paragraph paragraph = new Paragraph();

        if (paragraphBuilder != null && !paragraphBuilder.toString().trim().isEmpty()
                && !paragraphBuilder.toString().equalsIgnoreCase("null")) {
            String paragraphString = paragraphBuilder.toString();
            if (paragraphString.contains("Editor’s note")) {
                paragraph.isEditorsNote = true;
            }
            /*
                Dig deeper:
                For our latest coverage of the covid-19 pandemic, register for The Economist Today, our daily newsletter,
                or visit our coronavirus tracker and story hub
             */
            if (paragraphString.contains("For our latest")) {
                paragraph.isRelatedSuggestion = true;
            }
            paragraph.paragraph = paragraphString;
            return paragraph;
        }
        return null;
    }

    public static Issue getIssue(WeekJson weekJson) {
        Issue issue = new Issue();
        WeekSection weekSection = weekJson.data.section;
        CoverContent coverContent = weekSection.image.cover.get(0);
        issue.headline = coverContent.headline;
        issue.issueUrl = weekSection.url.canonical;
        issue.coverImageUrl = coverContent.url.canonical;
        issue.containArticle = getWholeArticle(weekJson);
        issue.issueDate = Utils.digitalDateSwitchToEnglishFormat(weekSection.datePublished.substring(0, 10));
        issue.issueFormatDate = weekSection.datePublished.substring(0, 10);
        List<String> sectionList = new ArrayList<>();
        for (Article a : issue.containArticle) {
            if (!sectionList.contains(a.section)) {
                sectionList.add(a.section);
            }
        }
        issue.categorySection = sectionList;
        sArticleLinkedHashMap = getArticleListBySection(issue);
        return issue;
    }

    public static List<Article> getTodayArticleList(TodayJson todayJson) {
        List<Article> articleList = new ArrayList<>();
        List<TodayFirstParts> todayFirstPartsList = todayJson.data.canonical.hasPart.parts;
        for (TodayFirstParts firstParts : todayFirstPartsList) {
            List<TodaySecondParts> todaySecondPartsList = firstParts.hasPart.parts;
            for (TodaySecondParts secondParts : todaySecondPartsList) {
                Article article = new Article();
                article.articleRubric = secondParts.rubric;
                article.title = secondParts.title;
                article.flyTitle = secondParts.flyTitle;
                List<Internal> internalList = secondParts.articleSection.internal;
                if (internalList != null) {
                    article.section = internalList.get(0).sectionName;
                }
                if (secondParts.audio != null) {
                    if (secondParts.audio.main != null) {
                        article.audioDuration = secondParts.audio.main.duration;
                        article.audioUrl = secondParts.audio.main.url.canonical;
                    }
                }
                article.mainArticleImage = secondParts.image.main.url.canonical;
                article.date = secondParts.published.substring(0, 10);
                article.paragraphList = new ArrayList<>();
                int theOrderOfParagraph = 0;
                StringBuilder articleBuilder = new StringBuilder();
                for (WeekText weekText : secondParts.text) {
                    // weekText0.children 第一个 children列表里data 字段, 合并成一个段落
                    StringBuilder paragraphBuilder = new StringBuilder();
                    // 3. 获取段落的所有数据
                    for (WeekText weekText1 : weekText.children) {
                        if (weekText1.children != null) {
                            List<WeekText> children = weekText1.children;
                            for (WeekText w : children) {
                                paragraphBuilder.append(w.data);
                            }
                        } else { //(weekText1.children == null)
                            if (weekText1.type.equals("text")) {
                                paragraphBuilder.append(weekText1.data);
                            }
                        }
                    }
                    Paragraph paragraph = filteredParagraph(paragraphBuilder);

                    if (paragraph != null) {
                        theOrderOfParagraph++;
                        paragraph.articleName = article.title;
                        paragraph.issueDate = article.date;
                        paragraph.belongedSection = article.section;
                        paragraph.theOrderOfParagraph = theOrderOfParagraph;
                        article.paragraphList.add(paragraph);
                    }
                    articleBuilder.append(paragraphBuilder);
                }
                articleList.add(article);
            }
        }
        return articleList;
    }

    public static LinkedHashMap<String, List<Article>> getArticleListBySection(Issue issue) {
        LinkedHashMap<String, List<Article>> articleLinkedHashMap = new LinkedHashMap<>();
        List<Article> articleList = issue.containArticle;
        List<Article> list = new ArrayList<>();
        for (Article article : articleList) {
            if (articleLinkedHashMap.get(article.section) == null) {
                list = new ArrayList<>();
            } else {
                list = articleLinkedHashMap.get(article.section);
            }
            list.add(article);
            articleLinkedHashMap.put(article.section, list);
        }
        return articleLinkedHashMap;
    }

    public static int getArticleSumBySection(int sectionPosition, Issue issue) {
        if (issue.categorySection == null
                || issue.categorySection.size() == 0) {
            return -1;
        }
        LinkedHashMap<String, List<Article>> maps = getArticleListBySection(issue);
        int i = 0;
        int sum = 0;
        for (Iterator pairs = maps.entrySet().iterator(); pairs.hasNext() && i < sectionPosition; i++) {
            Map.Entry pair = (Map.Entry) pairs.next();
            List<Article> articleList = (List<Article>) pair.getValue();
            sum += articleList.size();
        }
        return sum;
    }

    public static String getDurationFormat(float duration) {
        int minute = (int) (duration / 60);  // 63
        int seconds = (int) (duration % 60);
        if (minute < 10 && seconds < 10) {
            return minute + ":" + "0" + seconds;
        }
        if (minute < 10 && seconds >= 10) {
            return minute + ":" + seconds;
        }
        if (minute >= 10 && seconds >= 10) {
            return minute + ":" + seconds;
        }
        if (minute >= 10 && seconds < 10) {
            return minute + ":" + "0" + seconds;
        }
        return null;
    }

    public static String digitalDateSwitchToEnglishFormat(String dateString) {
        if (dateString.trim().equals("")) {
            return null;
        }
        //2020-05-09 ====>  May 9th 2020
        String[] dateArray = dateString.split("-");
        String year = dateArray[0];
        String day;
        switch (dateArray[2]) {
            case "01":
                day = "1st";
                break;
            case "02":
                day = "2nd";
                break;
            case "03":
                day = "3rd";
                break;
            case "21":
                day = "21st";
                break;
            case "22":
                day = "22nd";
                break;
            case "23":
                day = "23rd";
                break;
            case "31":
                day = "31st";
                break;
            default:
                String trimDay;
                if (dateArray[2].startsWith("0")) {
                    trimDay = dateArray[2].substring(1, 2);
                    day = trimDay + "th";
                } else {
                    day = dateArray[2] + "th";
                }
                break;
        }

        String month;
        switch (dateArray[1]) {
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
            default:
                month = "";
                break;
        }

        return month + " " + day + " " + year;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static List<Issue> getIssueList(Archive archiveData) {
        Part[] partArray = archiveData.data.section.hasPart.parts;
        List<Issue> issueList = new ArrayList<>();
        Log.d(TAG, "getIssueList: ");
        for (int i = 0; i < partArray.length; i++) {
            Issue issue = new Issue();
            issue.isDownloaded = false;
            String date = partArray[i].datePublished.substring(0, 10);
            issue.issueFormatDate = date;
            String urlId = partArray[i].id.split("/")[2];
            issue.urlID = urlId;
            issue.issueDate = Utils.digitalDateSwitchToEnglishFormat(date);
            issue.coverImageUrl = partArray[i].image.cover.get(0).url.canonical;
            issueList.add(issue);
        }
        return issueList;
    }
}
