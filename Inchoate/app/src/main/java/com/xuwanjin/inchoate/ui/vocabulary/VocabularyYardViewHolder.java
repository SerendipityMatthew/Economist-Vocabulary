package com.xuwanjin.inchoate.ui.vocabulary;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.ui.BaseViewHolder;


public class VocabularyYardViewHolder extends BaseViewHolder {
    public TextView vocabulary;
    public TextView sentence;
    public TextView articleInfo;

    public VocabularyYardViewHolder(@NonNull View itemView, boolean isHeaderOrFooter) {
        super(itemView, isHeaderOrFooter);
    }

    @Override
    protected void initView() {
        vocabulary = itemView.findViewById(R.id.vocabulary);
        sentence = itemView.findViewById(R.id.sentence);
        articleInfo = itemView.findViewById(R.id.article_info);
    }
}
