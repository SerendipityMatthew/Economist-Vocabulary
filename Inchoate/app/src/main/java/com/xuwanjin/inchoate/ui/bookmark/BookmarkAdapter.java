package com.xuwanjin.inchoate.ui.bookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.ui.BaseAdapter;

import java.util.List;


/**
 * @author Matthew Xu
 */
public class BookmarkAdapter extends BaseAdapter<BookmarkViewHolder, Article> {
    public static final String TAG = "BookmarkAdapter";


    public BookmarkAdapter(Context context, List<Article> articleList) {
        super(context, articleList);
    }

    //Item header 按照 section, issueDate, bookmarkDate, Topic,
    @Override
    public String getGroupName(int position) {
        Article article = mDataList.get(position);
        String groupName;
        String groupPolicy = bookmarkGroupPolicy();
        switch (groupPolicy) {
            case "issue_date":
            case "bookmark_date":
                groupName = article.date;
                break;
            case "section":
            default:
                groupName = article.section;
                break;
        }
        return groupName;
    }

    private String bookmarkGroupPolicy() {
        @SuppressLint("CommitPrefEdits") SharedPreferences editor =
                mContext.getSharedPreferences("inchoate", Context.MODE_PRIVATE);
        String bookmark_group_policy = editor.getString("bookmark_group_policy", "section");
        return bookmark_group_policy;
    }


    @Override
    public boolean isItemHeader(int position) {

        // position == 0 ,是inflater 进去的 HeaderView,
        // HeaderView 上面不能画一个 ItemHeaderView, 所以, 返回的是 false
        // 因为第一项是 HeaderView
        if (position == 0) {
            return true;
        }
        if (position == getItemCount() - 1) {
            return false;
        }

        // 因为我们有一个 HeaderView, 这个 Position 是
        // RecyclerView 里的是 List.size() +1 项, 为了数据对应. 这里的需要  position -2
        String lastGroupName = mDataList.get(position - 1).section;
        String currentGroupName = mDataList.get(position).section;
        if (lastGroupName.equals(currentGroupName)) {
            return false;
        }
        return true;
    }

    @Override
    protected int getLayoutItemResId() {
        return R.layout.bookmark_list;
    }

    @Override
    protected BookmarkViewHolder getViewHolder(View view, boolean isHeaderOrFooter) {
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolderImpl(@NonNull BookmarkViewHolder holder, final int position, Article article) {
        Glide.with(mContext)
                .load(article.mainArticleImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.the_economist)
                .override(700, 500)
                .into(holder.article_image);
        Glide.with(mContext)
                .load(R.mipmap.bookmark_black)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.bookmark);


        holder.article_title.setText(article.title);
        holder.articleFlyTitle.setText(article.flyTitle);
        holder.dateAndReadTime.setText(article.date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFakeData(article)) {
                    return;
                }
                InchoateApp.setDisplayArticleCache(mDataList.get(position));
                navigationToFragment(R.id.navigation_article);
            }
        });

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFakeData(article)) {
                    return;
                }
                Log.d(TAG, "onClick: position = " + position);
                article.isBookmark = !article.isBookmark;
                mDataList.remove(position);
                updateData(mDataList);
                InchoateDBHelper helper = InchoateDBHelper.getInstance(mContext);
                helper.setBookmarkStatus(article);
//                helper.close();
            }
        });
    }
    private boolean isFakeData(Article article){
        if (article == null
                || article.title == null
                || article.title.equals("")
                || article.paragraphList == null) {
            return true;
        }
        return false;
    }
}
