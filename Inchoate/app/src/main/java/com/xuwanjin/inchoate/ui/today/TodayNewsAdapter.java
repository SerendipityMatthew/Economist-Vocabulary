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
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
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
    public void onBindViewHolderImpl(@NonNull TodayNewsViewHolder holder, final int position, Article article) {
        Glide.with(mContext)
                .load(article.mainArticleImage)
                .placeholder(R.mipmap.the_economist)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.articleImage);

        Glide.with(mContext)
                .load(article.isBookmark ? R.mipmap.bookmark_black : R.mipmap.bookmark_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.bookmark_white)
                .into(holder.bookmark);

        holder.sectionText.setText(article.section);
        holder.title.setText(article.title);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (article.isBookmark) {
                    article.isBookmark = false;
                } else {
                    article.isBookmark = true;
                }
                mDataList.set(fetchPositionInDataList(position), article);
                InchoateDBHelper dbHelper = InchoateDBHelper.getInstance(mContext);
                dbHelper.setBookmarkStatus(article);
                notifyItemChanged(position);
            }
        });
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
        if (lastGroupName.equals(currentGroupName)) {
            return false;
        }
        return true;
    }
}
