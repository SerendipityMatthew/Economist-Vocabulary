package com.xuwanjin.inchoate.ui.previous;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.archive.Archive;
import com.xuwanjin.inchoate.model.archive.Part;
import com.xuwanjin.inchoate.model.week.WeekData;
import com.xuwanjin.inchoate.model.week.WeekFragment;
import com.xuwanjin.inchoate.model.week.WeekPart;
import com.xuwanjin.inchoate.model.week.WeekText;

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

public class PreviousFragment extends Fragment {
    RecyclerView issueListRecyclerView;
    GridLayoutManager mGridLayoutManager;
    PreviousAdapter previousAdapter;
    List<Issue> issueList;
    public static final int FETCH_DATA_AND_NOTIFY_MSG = 1000;
    public static final String TAG = "PreviousFragment";
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == FETCH_DATA_AND_NOTIFY_MSG) {
                if (previousAdapter != null) {
                    previousAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_previous, container, false);
        issueListRecyclerView = view.findViewById(R.id.issue_list_recyclerView);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        issueListRecyclerView.setLayoutManager(mGridLayoutManager);
        issueList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Issue issue = new Issue();
            issue.isDownloaded = false;
            issue.issueDate = "Matthew + " + i;
            issueList.add(issue);
        }
        previousAdapter = new PreviousAdapter(issueList, getContext());
        issueListRecyclerView.setAdapter(previousAdapter);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.ARCHIVE_QUERY_URL)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: e = " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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
                        }) // setFieldNamingPolicy 有什么区别
                        .create();
                Archive data = gson.fromJson(jsonResult, Archive.class);
                Log.d(TAG, "onResponse: rootValueAndData.data " + data.data);
                Part[] partArray = data.data.section.hasPart.parts;
                issueList.clear();
                for (int i = 0; i < partArray.length; i++) {
                    Issue issue = new Issue();
                    issue.isDownloaded = false;
                    issue.issueDate = Utils.digitalDateSwitchToEnglishFormat(partArray[i].datePublished.substring(0, 10));
                    issue.coverImageUrl = partArray[i].image.cover.get(0).url.canonical;
                    issueList.add(issue);
                }
                mHandler.sendEmptyMessage(FETCH_DATA_AND_NOTIFY_MSG);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
