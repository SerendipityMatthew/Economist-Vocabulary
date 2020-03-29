package com.xuwanjin.inchoate.ui.bookmark;

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

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment {
    RecyclerView bookmarkRecycleView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        bookmarkRecycleView = view.findViewById(R.id.bookmark_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        bookmarkRecycleView.setLayoutManager(gridLayoutManager);
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
        BookmarkAdapter adapter = new BookmarkAdapter(articles, getContext());
        bookmarkRecycleView.setAdapter(adapter);
    }
}
