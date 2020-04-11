package com.xuwanjin.inchoate.ui.weekly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.ui.bookmark.BookmarkAdapter;
import com.xuwanjin.inchoate.ui.bookmark.StickHeaderDecoration;

import java.util.ArrayList;
import java.util.List;

public class WeeklyFragment extends Fragment {
    RecyclerView issue_content_recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        issue_content_recyclerView = view.findViewById(R.id.issue_content_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        issue_content_recyclerView.setLayoutManager(gridLayoutManager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Article> articles = new ArrayList<>();
        for (int i =0; i< 60 ;i ++){
            Article article =  new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline= "Matthew = " + i;
            articles.add(article);
        }
        BookmarkAdapter adapter = new BookmarkAdapter(articles, getContext(), this);
        issue_content_recyclerView.setAdapter(adapter);
        issue_content_recyclerView.addItemDecoration(new StickHeaderDecoration(issue_content_recyclerView));

    }
}
