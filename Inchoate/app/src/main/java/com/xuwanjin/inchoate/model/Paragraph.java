package com.xuwanjin.inchoate.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Paragraph implements Parcelable {
    public String articleName;
    public String paragraph;
    public Paragraph(){

    }
    protected Paragraph(Parcel in) {
        articleName = in.readString();
        paragraph = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(articleName);
        dest.writeString(paragraph);
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

    @Override
    public String toString() {
        return "Paragraph{" +
                "articleName='" + articleName + '\'' +
                ", paragraph='" + paragraph + '\'' +
                '}';
    }
}
