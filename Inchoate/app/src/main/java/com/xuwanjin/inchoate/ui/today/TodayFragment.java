package com.xuwanjin.inchoate.ui.today;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.today.TodayJson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;

import static com.xuwanjin.inchoate.Utils.getTodayArticleList;

public class TodayFragment extends Fragment {
    public static final String TAG = "TodayFragment";
    View view = null;
    RecyclerView recyclerViewTodayNews;
    public TodayViewModel todayViewModel = new TodayViewModel();
    List<Article> todayArticleList;
    TodayNewsAdapter mTodayNewsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_today, container, false);
        parseJsonDataFromAsset();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewTodayNews = view.findViewById(R.id.today_news_recyclerView);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewTodayNews.setLayoutManager(manager);
        mTodayNewsAdapter = new TodayNewsAdapter(getContext(), todayArticleList);
        recyclerViewTodayNews.setAdapter(mTodayNewsAdapter);
    }

    public void parseJsonDataFromAsset() {
        Gson gson = new Gson()
                .newBuilder()
                .setFieldNamingStrategy(new FieldNamingStrategy() {
                    @Override
                    public String translateName(Field f) {
                        String name = f.getName();
                        if (name.contains("-")) {
                            return name.replaceAll("-", "");
                        }
                        return name;
                    }
                }) // setFieldNamingPolicy 有什么区别
                .create();

        InputStream jsonStream = getContext().getResources().openRawResource(R.raw.today_fragment_query);
        InputStreamReader reader = new InputStreamReader(jsonStream);
        TodayJson todayJson = gson.fromJson(reader, TodayJson.class);
        todayArticleList = getTodayArticleList(todayJson);
    }
}

