package com.xuwanjin.inchoate.ui.article;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Paragraph;

import java.util.List;

public class ArticleFragment extends Fragment {
    RecyclerView mArticleContentRV;
    public ArticleContentAdapter mArticleContentAdapter;
    public List<Paragraph> mParagraphList;
    public GridLayoutManager mGridLayoutManager;

    public View mArticleContentHeaderView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Article article = InchoateApplication.getDisplayArticleCache();
        if (article != null) {
            mParagraphList = article.paragraphList;
        }
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mArticleContentRV = view.findViewById(R.id.article_content_recyclerview);
        mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        mArticleContentRV.setLayoutManager(mGridLayoutManager);
        mArticleContentAdapter = new ArticleContentAdapter(getContext(), mParagraphList, view);
        ArticleItemDecoration articleItemDecoration = new ArticleItemDecoration(mArticleContentRV, getContext());
        mArticleContentRV.addItemDecoration(articleItemDecoration);
        mArticleContentRV.setAdapter(mArticleContentAdapter);
        mArticleContentHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_article_header_view, mArticleContentRV, false);
        mArticleContentAdapter.setHeaderView(mArticleContentHeaderView);
        TextView duration = mArticleContentHeaderView.findViewById(R.id.duration);
        TextView sectionAndDate = mArticleContentHeaderView.findViewById(R.id.section_and_date);
        TextView articleTitle = mArticleContentHeaderView.findViewById(R.id.article_title);
        articleTitle.setText(article.title);
        Log.d("Matthew", "onCreateView: article.date = "+ article.date);
        sectionAndDate.setText(article.section + "  |  " + article.date);

        duration.setText(getDurationFormat(article.audioDuration));

        ImageView articleCoverImage = mArticleContentHeaderView.findViewById(R.id.article_cover_image);
        Glide.with(getContext()).load(article.imageUrl).into(articleCoverImage);
        return view;
    }

    public static String getDurationFormat(float duration) {
        int minute = (int) (duration / 60);  // 63
        int seconds = (int) (duration % 60);
        if (minute < 10 && seconds < 10) {
            return "0" + minute + ":" + "0" + seconds;
        }
        if (minute < 10 && seconds > 10) {
            return "0" + minute + ":" + seconds;
        }
        if (minute > 10 && seconds > 10) {
            return  minute + ":" + seconds;
        }
        if (minute > 10 && seconds < 10) {
            return minute + ":" + "0:" + seconds;
        }
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
