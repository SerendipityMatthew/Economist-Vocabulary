package com.xuwanjin.inchoate.ui.today;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.today.TodayJson;
import com.xuwanjin.inchoate.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.xuwanjin.inchoate.Utils.getTodayArticleList;

public class TodayFragment extends BaseFragment {
    public static final String TAG = "TodayFragment";
    RecyclerView mRecyclerViewTodayNews;
    private static List<Article> sTodayArticleList = new ArrayList<>();
    private TodayNewsAdapter mTodayNewsAdapter;
    private Disposable mDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        mRecyclerViewTodayNews = view.findViewById(R.id.today_news_recyclerView);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerViewTodayNews.setLayoutManager(manager);
        mTodayNewsAdapter = new TodayNewsAdapter(getContext());
        mRecyclerViewTodayNews.setAdapter(mTodayNewsAdapter);
    }

    @Override
    public void loadData() {
        if (sTodayArticleList.size() > 0) {
            updateTodayFragment(sTodayArticleList);
        } else {
            initFakeDataAndUpdateUI();
            loadTodayArticleList();
        }
    }

    private void initFakeDataAndUpdateUI() {
        List<Article> articleList = initFakeData();
        updateTodayFragment(articleList);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_today;
    }

    @Override
    protected List<Article> initFakeData() {
        List<Article> articleList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Article article = new Article();
            article.flyTitle = "";
            article.title = "";
            article.articleUrl = "";
            article.imageUrl = "";
            articleList.add(article);
        }
        return articleList;
    }

    private void updateTodayFragment(List<Article> articleList) {
        mTodayNewsAdapter.updateData(articleList);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void loadTodayArticleList() {
        mDisposable = Single.create(new SingleOnSubscribe<List<Article>>() {
            @Override
            public void subscribe(SingleEmitter<List<Article>> emitter) throws Exception {
                List<Article> articleList = fetchDataFromDBOrNetwork();
                emitter.onSuccess(articleList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articles -> {
                    mTodayNewsAdapter.updateData(articles);
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        initFakeDataAndUpdateUI();
                        Log.d(TAG, "accept: throwable = " + throwable);
                    }
                });

    }

    protected List<Article> fetchDataFromDBOrNetwork() {
        String jsonResult = fetchJsonFromServer(Constants.TODAY_SECTION_QUERY_URL);
        Gson gson = getGsonInstance();
        TodayJson todayJson = gson.fromJson(jsonResult, TodayJson.class);
        sTodayArticleList = getTodayArticleList(todayJson);
        return sTodayArticleList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}

