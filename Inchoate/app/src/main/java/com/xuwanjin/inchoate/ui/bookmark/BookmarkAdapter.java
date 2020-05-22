package com.xuwanjin.inchoate.ui.bookmark;

import android.content.Context;
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
import com.xuwanjin.inchoate.R;
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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bookmark_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Article article = mArticleList.get(position);
        Log.d(TAG, "onBindViewHolder: article.mainArticleImage = " + article.mainArticleImage);
        Glide.with(mContext)

                .load(article.mainArticleImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.the_economist)
                .override(700, 500)
                .into(holder.article_image);
        holder.article_title.setText(article.title);
        holder.articleFlyTitle.setText(article.flyTitle);
        holder.dateAndReadTime.setText(article.date);
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
