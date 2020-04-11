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

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Section;

import java.util.ArrayList;
import java.util.List;

public class WeeklyFragment extends Fragment {
    RecyclerView issue_content_recyclerView;
    TextView mSectionTitle;
    private View mSectionHeaderView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        issue_content_recyclerView = view.findViewById(R.id.issue_content_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        issue_content_recyclerView.setLayoutManager(gridLayoutManager);
        mSectionTitle = view.findViewById(R.id.section_title);
        mSectionHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.weekly_section_header, issue_content_recyclerView, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Article> articles;
        articles = initData(new ArrayList<Article>());
        WeeklyAdapter adapter = new WeeklyAdapter(articles, getContext(), this);
        issue_content_recyclerView.setAdapter(adapter);
        adapter.setHeaderView(mSectionHeaderView);
        issue_content_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                recyclerView.getLayoutManager().findViewByPosition()
            }
        });
        issue_content_recyclerView.addItemDecoration(new StickHeaderDecoration(issue_content_recyclerView, getContext()));
    }

    public List<Article> initData(List<Article> articles) {
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.ASIA;
            articles.add(article);
        }
        for (int i = 0; i < 20; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.CHINA;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.EUROPE;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.BRIEFING;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.BUSINESS;
            articles.add(article);
        }
        for (int i = 0; i < 15; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.BOOKS_AND_ARTS;
            articles.add(article);
        }
        for (int i = 0; i < 5; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.BRITAIN;
            articles.add(article);
        }
        for (int i = 0; i < 9; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.GRAPHIC_DETAIL;
            articles.add(article);
        }
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline = "Matthew = " + i;
            article.section = Section.LETTERS;
            articles.add(article);
        }
        return articles;
    }
}
