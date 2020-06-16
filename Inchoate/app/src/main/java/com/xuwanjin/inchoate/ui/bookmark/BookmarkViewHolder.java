package com.xuwanjin.inchoate.ui.bookmark;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.ui.BaseViewHolder;

public class BookmarkViewHolder extends BaseViewHolder {
    TextView article_title;
    TextView articleFlyTitle;
    ImageView article_image;
    TextView dateAndReadTime;
    ImageView bookmark;
    public BookmarkViewHolder(@NonNull View itemView) {
        super(itemView, false);
    }

    @Override
    protected void initView() {
        article_title = itemView.findViewById(R.id.article_title);
        article_image = itemView.findViewById(R.id.article_image);
        dateAndReadTime = itemView.findViewById(R.id.date_and_read_time);
        articleFlyTitle = itemView.findViewById(R.id.article_fly_title);
        bookmark = itemView.findViewById(R.id.bookmark);
    }
}
