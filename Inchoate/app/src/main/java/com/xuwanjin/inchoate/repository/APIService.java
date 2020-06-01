package com.xuwanjin.inchoate.repository;

import android.util.Log;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.week.WeekJson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.xuwanjin.inchoate.Utils.getWholeArticle;

public class APIService {
    public static final String TAG = "APIService";
    public List<Article> mArticlesList;

    public List<Article> getArticleList() {
        OkHttpClient weekFragmentClient = new OkHttpClient();
        Request weekFragmentRequest = new Request.Builder()
                .url(Constants.WEEK_FRAGMENT_QUERY_05_30_URL)
                .build();
        Call call = weekFragmentClient.newCall(weekFragmentRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: e = " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonResult = response.body().string();
                Log.d(TAG, "onResponse: jsonResult: jsonResult = " + jsonResult);
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
                WeekJson weekJson = gson.fromJson(jsonResult, WeekJson.class);
                Log.d(TAG, "onResponse: FETCH_DATA_AND_NOTIFY_MSG = ");
                mArticlesList = getWholeArticle(weekJson);
            }
        });
        return mArticlesList;
    }

}
