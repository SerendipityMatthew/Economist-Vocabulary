package com.xuwanjin.inchoate.ui.weekly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.ArticleSection;

import java.util.ArrayList;
import java.util.List;

public class WeeklyFragment extends Fragment {
    RecyclerView issueContentRecyclerView;
    private View mSectionHeaderView;
    private View mFooterView;
    private TextView previousEdition;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        issueContentRecyclerView = view.findViewById(R.id.issue_content_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        issueContentRecyclerView.setLayoutManager(gridLayoutManager);
        // 这种 header 的出现, 他会 inflate 在 RecyclerView 的上面, 这个时候, 画第一个 item 的 header,
        //也会出现在 RecyclerView 的上面, 但是他会出现, HeaderView 的上面
        mSectionHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.weekly_section_header, issueContentRecyclerView, false);
        previousEdition = mSectionHeaderView.findViewById(R.id.previous_edition);
        previousEdition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigationControllerUtils(InchoateApplication.NAVIGATION_CONTROLLER, R.id.navigation_previous_edition);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Article> articles;
        articles = initData(new ArrayList<Article>());
        WeeklyAdapter adapter = new WeeklyAdapter(articles, getContext(), this);
        issueContentRecyclerView.setAdapter(adapter);
        adapter.setHeaderView(mSectionHeaderView);
//        adapter.setFooterView(mFooterView);
        issueContentRecyclerView.addItemDecoration(new StickHeaderDecoration(issueContentRecyclerView, getContext()));
    }

    public List<Article> initData(List<Article> articles) {
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + ArticleSection.THE_WORLD_THIS_WEEK;
            article.section = ArticleSection.THE_WORLD_THIS_WEEK;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + ArticleSection.LEADERS;
            article.section = ArticleSection.LEADERS;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + ArticleSection.LETTERS;
            article.section = ArticleSection.LETTERS;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.BRIEFING;
            article.headline = "Matthew = " + ArticleSection.BRIEFING;
            articles.add(article);
        }
        for (int i = 0; i < 20; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.UNITED_STATES;
            article.headline = "Matthew = " + ArticleSection.UNITED_STATES;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.THE_AMERICAS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.ASIA;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.CHINA;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 15; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.MIDDLE_EAST_AND_AFRICA;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.EUROPE;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.BRITAIN;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.INTERNATIONAL;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 15; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.BUSINESS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.FINANCE_AND_ECONOMICS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.SCIENCE_AND_TECHNOLOGY;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.BOOKS_AND_ARTS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 15; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.ECONOMICS_AND_FINANCIAL_INDICATORS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.GRAPHIC_DETAIL;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleSection.OBITUNARY;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }

        return articles;
    }
}
