package com.xuwanjin.inchoate.ui.today;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.ui.BaseViewHolder;

/**
 * @author Matthew Xu
 */
public class TodayNewsViewHolder extends BaseViewHolder {
    ImageView articleImage;
    TextView sectionText;
    TextView title;
    TextView readTime;
    ImageView play;
    ImageView bookmark;
    public TodayNewsViewHolder(@NonNull View itemView) {
        super(itemView, false);

    }

    @Override
    protected void initView() {
        articleImage = itemView.findViewById(R.id.article_image);
        sectionText = itemView.findViewById(R.id.section);
        title = itemView.findViewById(R.id.title);
        readTime = itemView.findViewById(R.id.read_time);
        play = itemView.findViewById(R.id.play);
        bookmark = itemView.findViewById(R.id.bookmark);
    }
}
