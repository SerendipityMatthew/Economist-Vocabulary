package com.xuwanjin.inchoate.ui.previous;

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

import com.google.gson.Gson;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.Part;
import com.xuwanjin.inchoate.model.RootValueAndData;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PreviousFragment extends Fragment {
    RecyclerView issueListRecyclerView;
    GridLayoutManager mGridLayoutManager;
    PreviousAdapter previousAdapter;
    List<Issue> issueList;
    public static final String TAG = "PreviousFragment";
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000){
                if (previousAdapter != null){
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
                Gson gson = new Gson().newBuilder().create();
                RootValueAndData data = gson.fromJson(jsonResult, RootValueAndData.class);
                Log.d(TAG, "onResponse: rootValueAndData.data " + data.data);
                Part[] partArray = data.data.section.hasPart.parts;
                issueList.clear();
                for (int i = 0; i < partArray.length; i++) {
                    Issue issue = new Issue();
                    issue.isDownloaded = false;
                    issue.issueDate = partArray[i].datePublished;
                    issue.coverImageUrl = partArray[i].image.cover.get(0).url.canonical;
                   issueList.add(issue);
                }
                mHandler.sendEmptyMessage(1000);
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
