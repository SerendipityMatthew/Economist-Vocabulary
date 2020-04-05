package com.xuwanjin.inchoate.ui.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;


import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> implements StickHeaderDecoration.StickHeaderInterface {
    private Context mContext;
    private List<Article> mArticleList;

    public BookmarkAdapter(List<Article> articles, Context context) {
        mContext = context;
        mArticleList = articles;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bookmark_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext).load(R.mipmap.article_image).into(holder.article_image);
        holder.article_title.setText("Matthew" + position);
        holder.dateAndReadTime.setText("Matthew" + position);
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
        ImageView article_image;
        TextView dateAndReadTime;
        ImageView bookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            article_title = itemView.findViewById(R.id.article_title);
            article_image = itemView.findViewById(R.id.article_image);
            dateAndReadTime = itemView.findViewById(R.id.date_and_read_time);
            bookmark = itemView.findViewById(R.id.bookmark);
        }

    }
}
