package com.xuwanjin.inchoate.database.dao.greendao;

import com.google.gson.Gson;
import com.xuwanjin.inchoate.model.Article;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArticleConverter implements PropertyConverter<List<Article>, String> {
    @Override
    public List<Article> convertToEntityProperty(String databaseValue) {
        List<String> stringList = Arrays.asList(databaseValue.split(","));
        List<Article> articleList = new ArrayList<>();
        for (String s:stringList){
            articleList.add(new Gson().fromJson(s, Article.class));
        }
        return articleList;
    }

    @Override
    public String convertToDatabaseValue(List<Article> entityProperty) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Article article:entityProperty){
            String str = new Gson().toJson(article);
            stringBuilder.append(str);
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }
}
