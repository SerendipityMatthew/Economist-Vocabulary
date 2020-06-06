package com.xuwanjin.inchoate.ui.today;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.today.TodayJson;
import com.xuwanjin.inchoate.ui.BaseFragment;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.xuwanjin.inchoate.Utils.getTodayArticleList;

public class TodayFragment extends BaseFragment {
    public static final String TAG = "TodayFragment";
    View view = null;
    RecyclerView recyclerViewTodayNews;
    public TodayViewModel todayViewModel = new TodayViewModel();
    private static List<Article> sTodayArticleList = new ArrayList<>();
    private TodayNewsAdapter mTodayNewsAdapter;
    private Disposable mDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_today, container, false);
        recyclerViewTodayNews = view.findViewById(R.id.today_news_recyclerView);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewTodayNews.setLayoutManager(manager);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void loadData() {
        if (sTodayArticleList.size() > 0) {
            updateTodayFragment(sTodayArticleList);
        } else {
            List<Article> articleList = initFakeData();
            updateTodayFragment(articleList);
            if (Utils.isNetworkAvailable(getContext())){
                loadTodayArticleList();
            }
        }
    }

    private List<Article> initFakeData() {
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
        mTodayNewsAdapter = new TodayNewsAdapter(getContext());
        recyclerViewTodayNews.setAdapter(mTodayNewsAdapter);
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
                List<Article> articleList = loadDataFromNetwork(emitter);
                emitter.onSuccess(articleList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()
                ).subscribe(articles -> {
                    mTodayNewsAdapter.updateData(articles);

                });

    }

    public List<Article> loadDataFromNetwork(SingleEmitter<List<Article>> emitter) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.TODAY_SECTION_QUERY_URL)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if (!response.isSuccessful()) {
                Log.d(TAG, "subscribe: today news json result response unsuccessfully! " +
                        "response code " + response.code());
                return null;
            }
            String jsonResult = response.body().string();
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
                    })
                    .create();
            TodayJson todayJson = gson.fromJson(jsonResult, TodayJson.class);
            sTodayArticleList = getTodayArticleList(todayJson);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

