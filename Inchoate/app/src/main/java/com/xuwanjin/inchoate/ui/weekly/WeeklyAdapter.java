package com.xuwanjin.inchoate.ui.weekly;

import android.content.Context;
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

/**
 * @author Matthew Xu
 */
public class WeeklyAdapter extends BaseAdapter<WeeklyViewHolder, Article> {

    public WeeklyAdapter(Context context) {
        super(context, null);
    }

    public boolean isFirstItem(int position) {
        if (mHeaderView != null && position == 1) {
            return true;
        } else if (mHeaderView == null && position == 0) {
            return true;
        }
        return false;
    }

    @Override
    protected int getLayoutItemResId() {
        return R.layout.weekly_content_list;
    }

    @Override
    protected WeeklyViewHolder getViewHolder(View view, boolean isHeaderOrFooter) {
        return new WeeklyViewHolder(view, isHeaderOrFooter);
    }

    @Override
    public void onBindViewHolderImpl(@NonNull WeeklyViewHolder holder, final int position, Article article) {
        Glide.with(mContext)
                .load(article.mainArticleImage)
                .error(R.mipmap.the_economist)
                .placeholder(R.mipmap.the_economist)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.article_image);
        holder.articleTitle.setText(article.title);
        holder.articleFlyTitle.setText(article.flyTitle);
        holder.dateAndReadTime.setText(position - 1 + " min read");
        View.OnClickListener viewArticleOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (article == null
                        || article.title == null
                        || article.title.equals("")
                        || article.paragraphList == null) {
                    return;
                }
                InchoateApp.setDisplayArticleCache(article);
                Utils.navigationController(
                        InchoateApp.NAVIGATION_CONTROLLER, R.id.navigation_article);
            }

        };
        holder.titleAndMainImage.setOnClickListener(viewArticleOnClickListener);

        Glide.with(mContext)
                .load(article.isBookmark ? R.mipmap.bookmark_black : R.mipmap.bookmark_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.bookmark_white)
                .into(holder.bookmark);

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (article.isBookmark) {
                    article.isBookmark = false;
                } else {
                    article.isBookmark = true;
                }
                Glide.with(mContext)
                        .load(article.isBookmark ? R.mipmap.bookmark_black : R.mipmap.bookmark_white)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.bookmark_white)
                        .into(holder.bookmark);

                InchoateDBHelper dbHelper = InchoateDBHelper.getInstance(mContext);
                dbHelper.setBookmarkStatus(article, article.isBookmark);
//                    dbHelper.close();
            }
        });
    }


    @Override
    public String getGroupName(int position) {
        int pos = position;
        if (mHeaderView != null) {
            pos = position - 1;
        }
        return mDataList.get(pos).section;
    }

    // 判断当前的 position 对应的 item1 是否是相应的组的第一项
    @Override
    public boolean isItemHeader(int position) {
        // position == 1 ,以及之后的才是 item1, item2, item3, item3
        if (position == 1) {
            return true;
        }
        // position == 0 ,是inflater 进去的 HeaderView,
        // HeaderView 上面不能画一个 ItemHeaderView, 所以, 返回的是 false
        // 因为第一项是 HeaderView
        if (position == 0) {
            return false;
        }
        if (position == getItemCount() - 1) {
            return false;
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
