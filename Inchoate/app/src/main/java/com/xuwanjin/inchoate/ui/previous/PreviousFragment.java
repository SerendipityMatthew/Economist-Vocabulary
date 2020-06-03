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
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.archive.Archive;
import com.xuwanjin.inchoate.model.archive.Part;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Scheduler;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.single.SingleAmb;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PreviousFragment extends Fragment {
    public static final String TAG = "PreviousFragment";

    RecyclerView issueListRecyclerView;
    GridLayoutManager mGridLayoutManager;
    PreviousAdapter previousAdapter;
    public static List<Issue> sIssueList = new ArrayList<>();
    public static final int FETCH_DATA_AND_NOTIFY_MSG = 1000;
    private Disposable mDisposable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_previous, container, false);
        issueListRecyclerView = view.findViewById(R.id.issue_list_recyclerView);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        issueListRecyclerView.setLayoutManager(mGridLayoutManager);
        previousAdapter = new PreviousAdapter(getContext());
        issueListRecyclerView.setAdapter(previousAdapter);

        if (sIssueList != null && sIssueList.size() > 0) {
            Log.d(TAG, "onCreateView: sIssueList  = " + sIssueList.size());
            updatePreviousFragmentContent(sIssueList);
        } else {
            List<Issue> issueList = initFakeData();
            updatePreviousFragmentContent(issueList);
            loadPreviousIssue();
        }
        return view;
    }

    private List<Issue> initFakeData() {
        List<Issue> issueList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Issue issue = new Issue();
            issue.isDownloaded = false;
            issue.issueDate = "Matthew + " + i;
            issueList.add(issue);
        }
        return issueList;
    }

    public void loadPreviousIssue() {
        mDisposable = SingleAmb.create(new SingleOnSubscribe<List<Issue>>() {
            @Override
            public void subscribe(SingleEmitter<List<Issue>> emitter) throws Exception {

                sIssueList = getPreviousIssueDataFromNetwork();
                Log.d(TAG, "loadPreviousIssue: subscribe: sIssueList = " + sIssueList.size());
                emitter.onSuccess(sIssueList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Issue>>() {
                    @Override
                    public void accept(List<Issue> issueList) throws Exception {
                        updatePreviousFragmentContent(issueList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updatePreviousFragmentContent(initFakeData());
                    }
                });
    }

    private List<Issue> getPreviousIssueDataFromNetwork() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.ARCHIVE_QUERY_URL)
                .build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || !response.isSuccessful()) {
            return null;
        }
        String jsonResult = null;
        try {
            jsonResult = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonResult == null) {
            return null;
        }
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
        Part[] partArray = data.data.section.hasPart.parts;
        sIssueList.clear();
        Log.d(TAG, "getPreviousIssueDataFromNetwork: ");
        for (int i = 0; i < partArray.length; i++) {
            Issue issue = new Issue();
            issue.isDownloaded = false;
            String date = partArray[i].datePublished.substring(0, 10);
            issue.issueFormatDate = date;
            String urlId = partArray[i].id.split("/")[2];
            issue.urlID = urlId;
            issue.issueDate = Utils.digitalDateSwitchToEnglishFormat(date);
            issue.coverImageUrl = partArray[i].image.cover.get(0).url.canonical;
            sIssueList.add(issue);
        }
        return sIssueList;
    }

    private void updatePreviousFragmentContent(List<Issue> issueList) {
        previousAdapter.updateData(issueList);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
