package com.xuwanjin.inchoate.ui.bookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;


import java.util.IllegalFormatCodePointException;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder>
        implements StickHeaderDecoration.StickHeaderInterface {
    public static final String TAG = "BookmarkAdapter";
    private Context mContext;
    private List<Article> mArticleList;
    private Fragment mFragment;

    public BookmarkAdapter(List<Article> articles, Context context, Fragment fragment) {
        mContext = context;
        mArticleList = articles;
        mFragment = fragment;
    }

    public Fragment getFragment() {
        return mFragment;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bookmark_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Article article = mArticleList.get(position);
        Glide.with(mContext)
                .load(article.mainArticleImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.the_economist)
                .override(700, 500)
                .into(holder.article_image);
        holder.article_title.setText(article.title);
        holder.articleFlyTitle.setText(article.flyTitle);
        holder.dateAndReadTime.setText(article.date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InchoateApplication.setDisplayArticleCache(mArticleList.get(position));
                Utils.navigationController(
                        InchoateApplication.NAVIGATION_CONTROLLER, R.id.navigation_article);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView article_title;
        TextView articleFlyTitle;
        ImageView article_image;
        TextView dateAndReadTime;
        ImageView bookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            article_title = itemView.findViewById(R.id.article_title);
            article_image = itemView.findViewById(R.id.article_image);
            dateAndReadTime = itemView.findViewById(R.id.date_and_read_time);
            articleFlyTitle = itemView.findViewById(R.id.article_fly_title);
            bookmark = itemView.findViewById(R.id.bookmark);
        }

    }
}
