package com.xuwanjin.inchoate.ui.today;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.databinding.FragmentTodayBinding;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.today.TodayJson;
import com.xuwanjin.inchoate.ui.BaseFragment;
import com.xuwanjin.inchoate.viewmodel.BaseViewModel;
import com.xuwanjin.inchoate.viewmodel.TodayNewsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.xuwanjin.inchoate.utils.Utils.getTodayArticleList;

/**
 * @author Matthew Xu
 */
public class TodayNewsFragment extends BaseFragment<TodayNewsAdapter, TodayItemDecoration, List<Article>, GridLayoutManager, FragmentTodayBinding, TodayNewsViewModel> {
    public static final String TAG = "TodayFragment";
    private static List<Article> sTodayArticleList = new ArrayList<>();
    private Disposable mDisposable;
    private static ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected Class<TodayNewsViewModel> getViewModel() {
        return TodayNewsViewModel.class;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView = mBaseViewDataBinding.todayNewsRecyclerView;
        mLayoutManager = new GridLayoutManager(getContext(), 1);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mBaseAdapter = new TodayNewsAdapter(getContext());
        mRecyclerView.setAdapter(mBaseAdapter);
        mBaseItemDecoration = new TodayItemDecoration(getContext(), mRecyclerView);
        mRecyclerView.addItemDecoration(mBaseItemDecoration);
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
            article.headline = "Matthew";
            article.articleUrl = "";
            article.imageUrl = "";
            articleList.add(article);
        }
        return articleList;
    }

    private void updateTodayFragment(List<Article> articleList) {
        mBaseAdapter.updateData(articleList);
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
                .subscribe(articleList -> {
                    mBaseAdapter.updateData(articleList);
                    updateDatabase(articleList);
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        initFakeDataAndUpdateUI();
                        Log.d(TAG, "accept: throwable = " + throwable);
                    }
                });

    }

    public void updateDatabase(List<Article> articleList) {
        Runnable mInsertIssueData = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "mInsertIssueData: run: ");
                InchoateDBHelper helper = InchoateDBHelper.getInstance(getContext());
                for (Article article : articleList) {
                    ((InchoateApp)(getContext().getApplicationContext())).getDaoSession().getArticleDao().insert(article);
//                    helper.insertArticle(article);
                }
            }
        };
        mExecutorService.schedule(mInsertIssueData, 10, TimeUnit.SECONDS);
        // 延迟插入数据, 防止线程竞争打开数据库的问题.
    }

    @Override
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

