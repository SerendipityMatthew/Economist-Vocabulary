package com.xuwanjin.inchoate.ui.bookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;

import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkViewHolder>
        implements StickHeaderDecoration.StickHeaderInterface {
    public static final String TAG = "BookmarkAdapter";
    private Context mContext;
    private List<Article> mArticleList = new ArrayList<>();

    public BookmarkAdapter(Context context) {
        mContext = context;
    }

    public void updateData(List<Article> articleList) {
        mArticleList.clear();
        mArticleList.addAll(articleList);
        notifyDataSetChanged();
    }

    //Item header 按照 section, issueDate, bookmarkDate, Topic,
    public String getGroupName(int position) {
        Article article = mArticleList.get(position);
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


    public boolean isItemHeader(int position) {

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
        String lastGroupName = mArticleList.get(position - 1).section;
        String currentGroupName = mArticleList.get(position).section;
        if (lastGroupName.equals(currentGroupName)) {
            return false;
        }
        return true;
    }

    @NonNull
    @Override
    public  BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bookmark_list, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, final int position) {

        Article article = mArticleList.get(position);
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
                InchoateApp.setDisplayArticleCache(mArticleList.get(position));
                Utils.navigationController(
                        InchoateApp.NAVIGATION_CONTROLLER, R.id.navigation_article);
            }
        });

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
                InchoateDBHelper helper = InchoateDBHelper.getInstance(mContext);
                helper.setBookmarkStatus(article, article.isBookmark);
//                helper.close();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mArticleList == null ? 0 : mArticleList.size();
    }

    @Override
    public boolean isStick(int position) {
        if ((position % 8 == 0)) {
            return true;
        }
        return false;
    }

}
