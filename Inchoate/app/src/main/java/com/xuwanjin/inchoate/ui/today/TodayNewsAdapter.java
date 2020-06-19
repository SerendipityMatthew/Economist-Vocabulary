package com.xuwanjin.inchoate.ui.today;

import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.ui.BaseAdapter;

import java.util.List;

/**
 * @author Matthew Xu
 */
public class TodayNewsAdapter extends BaseAdapter<TodayNewsViewHolder, Article> {
    public static final String TAG = "TodayNewsAdapter";

    public TodayNewsAdapter(Context context) {
        super(context, null);
    }

    @Override
    protected int getLayoutItemResId() {
        return R.layout.today_news_item;
    }

    @Override
    protected TodayNewsViewHolder getViewHolder(View view, boolean isHeaderOrFooter) {
        return new TodayNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolderImpl(@NonNull TodayNewsViewHolder holder, final int position) {
        final Article article = mDataList.get(position);
        Glide.with(mContext)
                .load(article.mainArticleImage)
                .placeholder(R.mipmap.the_economist)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.articleImage);
        holder.sectionText.setText(article.section);
        holder.title.setText(article.title);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: article = " + article);
                if (article.section != null && !"null".equalsIgnoreCase(article.section)) {
                    InchoateApp.setDisplayArticleCache(article);
                    Utils.navigationController(
                            InchoateApp.NAVIGATION_CONTROLLER, R.id.navigation_article);
                }
            }
        };
        holder.articleImage.setOnClickListener(onClickListener);
        holder.sectionText.setOnClickListener(onClickListener);
        holder.title.setOnClickListener(onClickListener);
    }

    public List<Article> getTodayNewsArticleList() {
        return mDataList;
    }

    @Override
    public String getGroupName(int position) {
        return mDataList.get(position).headline;
    }

    @Override
    public boolean isItemHeader(int position) {
        if (position == 0) {
            return true;
        }
        // 因为我们有一个 HeaderView, 这个 Position 是
        // RecyclerView 里的是 List.size() +1 项, 为了数据对应. 这里的需要  position -2
        String lastGroupName = getGroupName(position - 1);
        String currentGroupName = getGroupName(position);
        Log.d(TAG, "isItemHeader: lastGroupName = " + lastGroupName + ", currentGroupName " + currentGroupName);
        if (lastGroupName.equals(currentGroupName)) {
            return false;
        }
        return true;
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

}
