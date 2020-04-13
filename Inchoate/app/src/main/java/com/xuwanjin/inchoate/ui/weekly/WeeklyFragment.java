package com.xuwanjin.inchoate.ui.weekly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Section;

import java.util.ArrayList;
import java.util.List;

public class WeeklyFragment extends Fragment {
    RecyclerView issueContentRecyclerView;
    private View mSectionHeaderView;
    private View mFooterView;

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
            article.headline = "Matthew = " + Section.THE_WORLD_THIS_WEEK;
            article.section = Section.THE_WORLD_THIS_WEEK;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + Section.LEADERS;
            article.section = Section.LEADERS;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + Section.LETTERS;
            article.section = Section.LETTERS;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.BRIEFING;
            article.headline = "Matthew = " + Section.BRIEFING;
            articles.add(article);
        }
        for (int i = 0; i < 20; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.UNITED_STATES;
            article.headline = "Matthew = " + Section.UNITED_STATES;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.THE_AMERICAS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.ASIA;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.CHINA;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 15; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.MIDDLE_EAST_AND_AFRICA;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.EUROPE;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.BRITAIN;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.INTERNATIONAL;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 15; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.BUSINESS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.FINANCE_AND_ECONOMICS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.SCIENCE_AND_TECHNOLOGY;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.BOOKS_AND_ARTS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 15; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.ECONOMICS_AND_FINANCIAL_INDICATORS;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.GRAPHIC_DETAIL;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = Section.OBITUNARY;
            article.headline = "Matthew = " + article.section;
            articles.add(article);
        }

        return articles;
    }
}
