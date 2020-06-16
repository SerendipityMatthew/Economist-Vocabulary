package com.xuwanjin.inchoate.ui.today;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;

import java.util.ArrayList;
import java.util.List;

public class TodayNewsAdapter extends RecyclerView.Adapter<TodayNewsViewHolder> {
    public static final String TAG = "TodayNewsAdapter";
    private List<Article> mTodayNewsArticleList = new ArrayList<>();
    private Context mContext;

    public TodayNewsAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public TodayNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.today_news_item, parent, false);
        return new TodayNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayNewsViewHolder holder, final int position) {
        final Article article = mTodayNewsArticleList.get(position);
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
        return mTodayNewsArticleList;
    }

    public String getGroupName(int position) {
        return mTodayNewsArticleList.get(position).headline;
    }

    public boolean isItemHeader(int position) {
        if (position == 0) {
            return true;
        }
        // 因为我们有一个 HeaderView, 这个 Position 是
        // RecyclerView 里的是 List.size() +1 项, 为了数据对应. 这里的需要  position -2
        String lastGroupName = mTodayNewsArticleList.get(position - 1).headline;
        String currentGroupName = mTodayNewsArticleList.get(position).headline;
        Log.d(TAG, "isItemHeader: lastGroupName = " + lastGroupName + ", currentGroupName " + currentGroupName);
        if (lastGroupName.equals(currentGroupName)) {
            return false;
        }
        return true;
    }


    @Override
    public int getItemCount() {
        return mTodayNewsArticleList == null ? 0 : mTodayNewsArticleList.size();
    }

    public void updateData(List<Article> articleList) {
        mTodayNewsArticleList.clear();
        mTodayNewsArticleList.addAll(articleList);
        notifyDataSetChanged();
    }
}
