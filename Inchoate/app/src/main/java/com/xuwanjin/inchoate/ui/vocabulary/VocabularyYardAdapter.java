package com.xuwanjin.inchoate.ui.vocabulary;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Vocabulary;
import com.xuwanjin.inchoate.ui.BaseAdapter;

import java.util.List;

/**
 * @author Matthew Xu
 */
public class VocabularyYardAdapter extends BaseAdapter<VocabularyYardViewHolder, Vocabulary> {
    public static final String TAG = "VocabularyYardAdapter";
    protected VocabularyYardAdapter(Context context, List<Vocabulary> dataList) {
        super(context, dataList);
    }

    @Override
    protected int getLayoutItemResId() {
        return R.layout.vocabulary_item;
    }

    @Override
    protected VocabularyYardViewHolder getViewHolder(View view, boolean isHeaderOrFooter) {
        return new VocabularyYardViewHolder(view, isHeaderOrFooter);
    }

    @Override
    public String getGroupName(int position) {
        return null;
    }

    @Override
    protected void onBindViewHolderImpl(VocabularyYardViewHolder holder, int position, Vocabulary vocabulary) {
        holder.vocabulary.setText(vocabulary.vocabularyContent);
        if (vocabulary.belongedSentence != null){
            holder.sentence.setText(vocabulary.belongedSentence);
        }
        if (vocabulary.belongedArticleTitle != null){
            holder.articleInfo.setText(vocabulary.belongedArticleTitle);
        }
    }

    @Override
    public boolean isItemHeader(int position) {
        return false;
    }
}
