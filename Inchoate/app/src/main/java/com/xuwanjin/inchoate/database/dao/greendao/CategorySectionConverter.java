package com.xuwanjin.inchoate.database.dao.greendao;


import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategorySectionConverter implements PropertyConverter<List<String>, String> {
    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        List<String> stringList = Arrays.asList(databaseValue.split(","));
        List<String> paragraphList = new ArrayList<>();
        for (String s : stringList) {
            paragraphList.add(new Gson().fromJson(s, String.class));
        }
        return paragraphList;
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String section : entityProperty) {
            String str = new Gson().toJson(section);
            stringBuilder.append(str);
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }
}
