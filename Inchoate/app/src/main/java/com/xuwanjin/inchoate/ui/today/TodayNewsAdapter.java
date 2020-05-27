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

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;

import java.util.List;

public class TodayNewsAdapter extends RecyclerView.Adapter<TodayNewsAdapter.ViewHolder> {
    public static final String TAG = "TodayNewsAdapter";
    private List<Article> mArticleList;
    private Context mContext;

    public TodayNewsAdapter(Context context, List<Article> articles) {
        mArticleList = articles;
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
        final Article article = mArticleList.get(position);
        Glide.with(mContext).load(article.mainArticleImage).into(holder.articleImage);
        holder.sectionText.setText(article.section);
        holder.title.setText(article.title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InchoateApplication.setDisplayArticleCache(article);
                Utils.navigationController(
                        InchoateApplication.NAVIGATION_CONTROLLER, R.id.navigation_article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArticleList == null ? 0 : mArticleList.size();
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
