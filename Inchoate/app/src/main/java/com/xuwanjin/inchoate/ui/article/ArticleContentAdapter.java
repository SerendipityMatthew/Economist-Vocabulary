package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.ui.BaseAdapter;

import java.util.List;

public class ArticleContentAdapter extends BaseAdapter<ArticleParagraphViewHolder, Paragraph> {
    public static final String TAG = "ArticleContentAdapter";
    private Article mArticle;

    public ArticleContentAdapter(Context context, List<Paragraph> paragraphList) {
        super(context, paragraphList);
    }

    public void setArticle(Article article){
        this.mArticle = article;
    }

    @Override
    protected int getLayoutItemResId() {
        return R.layout.article_content_item;
    }

    @Override
    protected ArticleParagraphViewHolder getViewHolder(View view, boolean isHeaderOrFooter) {
        return new ArticleParagraphViewHolder(view, mContext, mArticle, isHeaderOrFooter);
    }

    @Override
    public String getGroupName(int position) {
        return null;
    }

    @Override
    public void onBindViewHolderImpl(@NonNull ArticleParagraphViewHolder holder, int position) {
        if (position >= 1 && position < getItemCount() - 1) {
            Paragraph paragraph = mDataList.get(position - 1);
            holder.paragraphTextView.setText(paragraph.paragraph, TextView.BufferType.SPANNABLE);
            holder.setCurrentParagraph(paragraph);
        }
    }

    @Override
    public boolean isItemHeader(int position) {
        return false;
    }
}
