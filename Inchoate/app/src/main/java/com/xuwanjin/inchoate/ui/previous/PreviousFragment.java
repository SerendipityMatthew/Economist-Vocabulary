package com.xuwanjin.inchoate.ui.previous;

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
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.archive.Archive;
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

public class PreviousFragment extends BaseFragment {
    public static final String TAG = "PreviousFragment";

    RecyclerView mIssueListRecyclerView;
    GridLayoutManager mGridLayoutManager;
    PreviousAdapter mPreviousAdapter;
    public static List<Issue> sIssueList = new ArrayList<>();
    private Disposable mDisposable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        mIssueListRecyclerView = view.findViewById(R.id.issue_list_recyclerView);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mIssueListRecyclerView.setLayoutManager(mGridLayoutManager);
        mPreviousAdapter = new PreviousAdapter(getContext());
        mIssueListRecyclerView.setAdapter(mPreviousAdapter);
    }

    @Override
    public void loadData() {
        if (sIssueList != null && sIssueList.size() > 0) {
            Log.d(TAG, "onCreateView: sIssueList  = " + sIssueList.size());
            updatePreviousFragmentContent(sIssueList);
        } else {
            List<Issue> issueList = initFakeData();
            updatePreviousFragmentContent(issueList);
            loadPreviousIssue();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_previous;
    }

    @Override
    protected List<Issue> initFakeData() {
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
        mDisposable = Single.create(new SingleOnSubscribe<List<Issue>>() {
            @Override
            public void subscribe(SingleEmitter<List<Issue>> emitter) throws Exception {
                sIssueList = fetchDataFromDBOrNetwork();
                Log.d(TAG, "loadPreviousIssue: subscribe: sIssueList = " + sIssueList.size());
                emitter.onSuccess(sIssueList);
            }
        })
                .subscribeOn(Schedulers.io())
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

    @Override
    protected List<Issue> fetchDataFromDBOrNetwork() {
        String jsonResult = fetchJsonFromServer(Constants.ARCHIVE_QUERY_URL);
        Gson gson = getGsonInstance();
        Archive data = gson.fromJson(jsonResult, Archive.class);
        sIssueList = Utils.getIssueList(data);
        return sIssueList;
    }

    private void updatePreviousFragmentContent(List<Issue> issueList) {
        mPreviousAdapter.updateData(issueList);
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
