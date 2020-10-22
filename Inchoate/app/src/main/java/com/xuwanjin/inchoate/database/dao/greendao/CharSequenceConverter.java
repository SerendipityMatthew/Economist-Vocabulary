package com.xuwanjin.inchoate.database.dao.greendao;

import org.greenrobot.greendao.converter.PropertyConverter;

public class CharSequenceConverter implements PropertyConverter<CharSequence, String> {
    @Override
    public CharSequence convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            CharSequence charSequence = databaseValue;
            return charSequence;
        }
    }

    @Override
    public String convertToDatabaseValue(CharSequence entityProperty) {
        return entityProperty.toString();
    }
}
