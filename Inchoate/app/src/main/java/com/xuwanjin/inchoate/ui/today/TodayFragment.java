package com.xuwanjin.inchoate.ui.today;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;

import java.util.ArrayList;
import java.util.List;

public class TodayFragment extends Fragment {
    View view = null;
    RecyclerView recyclerViewTodayNews;
    public TodayViewModel todayViewModel = new TodayViewModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_today, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewTodayNews = view.findViewById(R.id.today_news_recyclerView);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewTodayNews.setLayoutManager(manager);
        List<Article> articles = new ArrayList<>();
        for (int i =0; i< 21 ;i ++){
            Article article =  new Article();
            article.summary = "heeeeeeeeee" + i;
            article.headline= "Matthew = " + i;
            articles.add(article);

        }
        recyclerViewTodayNews.setAdapter(new TodayNewsAdapter(getContext(),articles ));
        recyclerViewTodayNews.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 30;
                if (parent.getChildPosition(view) == 0)
                    outRect.top = 10;
            }
        });
    };
}

