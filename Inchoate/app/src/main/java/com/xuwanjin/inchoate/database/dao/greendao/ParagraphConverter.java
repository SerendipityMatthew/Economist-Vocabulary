package com.xuwanjin.inchoate.database.dao.greendao;

import com.google.gson.Gson;
import com.xuwanjin.inchoate.model.Paragraph;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParagraphConverter implements PropertyConverter<List<Paragraph>, String> {
    @Override
    public List<Paragraph> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            List<String> stringList = Arrays.asList(databaseValue.split(","));
            List<Paragraph> paragraphList = new ArrayList<>();
            for (String s : stringList) {
                paragraphList.add(new Gson().fromJson(s, Paragraph.class));
            }
            return paragraphList;
        }
    }

    @Override
    public String convertToDatabaseValue(List<Paragraph> entityPropertyList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Paragraph paragraph : entityPropertyList) {
            String str = new Gson().toJson(paragraph);
            stringBuilder.append(str);
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }
}
