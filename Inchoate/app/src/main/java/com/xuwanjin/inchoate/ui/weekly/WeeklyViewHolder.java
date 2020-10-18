package com.xuwanjin.inchoate.ui.weekly;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.ui.BaseViewHolder;

class WeeklyViewHolder extends BaseViewHolder {
    TextView articleTitle;
    TextView articleFlyTitle;
    ImageView article_image;
    TextView dateAndReadTime;
    ImageView bookmark;
    View titleAndMainImage;

    public WeeklyViewHolder(@NonNull View itemView, boolean isHeaderOrFooter) {
        super(itemView, false);
    }

    @Override
    protected void initView() {
        titleAndMainImage = itemView.findViewById(R.id.title_and_image_container);
        articleTitle = itemView.findViewById(R.id.article_title);
        articleFlyTitle = itemView.findViewById(R.id.article_fly_title);
        article_image = itemView.findViewById(R.id.article_image);
        dateAndReadTime = itemView.findViewById(R.id.date_and_read_time);
        bookmark = itemView.findViewById(R.id.bookmark);
    }
}