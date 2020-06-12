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

public class TodayNewsAdapter extends RecyclerView.Adapter<TodayNewsAdapter.ViewHolder> {
    public static final String TAG = "TodayNewsAdapter";
    private List<Article> mTodayNewsArticleList = new ArrayList<>();
    private Context mContext;

    public TodayNewsAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.today_news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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
                if (article.section != null && !"null".equalsIgnoreCase(article.section)){
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

    @Override
    public int getItemCount() {
        return mTodayNewsArticleList == null ? 0 : mTodayNewsArticleList.size();
    }
    public void updateData(List<Article> articleList){
        mTodayNewsArticleList.clear();
        mTodayNewsArticleList.addAll(articleList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        TextView sectionText;
        TextView title;
        TextView readTime;
        ImageView play;
        ImageView bookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.article_image);
            sectionText = itemView.findViewById(R.id.section);
            title = itemView.findViewById(R.id.title);
            readTime = itemView.findViewById(R.id.read_time);
            play = itemView.findViewById(R.id.play);
            bookmark = itemView.findViewById(R.id.bookmark);
        }
    }
}
