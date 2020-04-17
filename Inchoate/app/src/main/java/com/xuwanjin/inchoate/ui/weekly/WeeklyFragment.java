package com.xuwanjin.inchoate.ui.weekly;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.ArticleCategorySection;
import com.xuwanjin.inchoate.model.week.WeekFragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.xuwanjin.inchoate.Utils.getWholeArticle;

public class WeeklyFragment extends Fragment {
    public static final String TAG = "WeekFragment";
    RecyclerView issueContentRecyclerView;
    private View mSectionHeaderView;
    private View mFooterView;
    private TextView previousEdition;
    List<Article> mArticlesList;
    WeeklyAdapter mWeeklyAdapter;
    StickHeaderDecoration mStickHeaderDecoration;
    public static final int FETCH_DATA_AND_NOTIFY_MSG = 1000;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == FETCH_DATA_AND_NOTIFY_MSG) {
                if (mWeeklyAdapter != null) {
                    mWeeklyAdapter.notifyDataSetChanged();
                }
            }
        }
    };

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
        mArticlesList = initData(new ArrayList<Article>());

        mWeeklyAdapter = new WeeklyAdapter(mArticlesList, getContext(), this);
        issueContentRecyclerView.setAdapter(mWeeklyAdapter);
        mWeeklyAdapter.setHeaderView(mSectionHeaderView);
//        adapter.setFooterView(mFooterView);
        mStickHeaderDecoration = new StickHeaderDecoration(issueContentRecyclerView, getContext());
        issueContentRecyclerView.addItemDecoration(new StickHeaderDecoration(issueContentRecyclerView, getContext()));

        weekFragmentTest();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private List<Article> initData(ArrayList<Article> articles) {
        for (int i = 0; i < 82; i++) {
            Article article = new Article();
            article.summary = "heeeeeeeeee" + i;
            article.section = ArticleCategorySection.BRIEFING.toString();
            article.headline = "Matthew = " + ArticleCategorySection.BRIEFING;
            articles.add(article);
        }
        return articles;
    }

    public void weekFragmentTest() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.WEEK_FRAGMENT_QUERY_URL)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "weekFragmentTest: onFailure: e = " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonResult = response.body().string();
//                Log.d(TAG, "onResponse: jsonResult: jsonResult = " + jsonResult);
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
                WeekFragment weekFragment = gson.fromJson(jsonResult, WeekFragment.class);
                Log.d(TAG, "onResponse: FETCH_DATA_AND_NOTIFY_MSG = ");
                mArticlesList.clear();
                mArticlesList.addAll(getWholeArticle(weekFragment));
//                Log.d(TAG, "onResponse: mArticlesList = " + mArticlesList.get(0));
                mHandler.sendEmptyMessage(FETCH_DATA_AND_NOTIFY_MSG);
            }
        });
    }

}
