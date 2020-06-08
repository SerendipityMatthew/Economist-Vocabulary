package com.xuwanjin.inchoate.ui.floataction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FloatActionFragment extends BaseFragment {
    public static final String TAG = "FloatActionFragment";
    private View mHeaderSectionView;
    private List<String> mSectionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater,container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.float_action_recyclerView);
        mHeaderSectionView = LayoutInflater.from(getContext()).inflate(R.layout.float_action_header, recyclerView, false);
        IssueCategoryAdapter categoryAdapter = new IssueCategoryAdapter(
                getContext(), mSectionList);
        categoryAdapter.setHeaderView(mHeaderSectionView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new IssueCategoryStickHeaderDecoration(recyclerView, getContext()));
        recyclerView.setAdapter(categoryAdapter);
    }

    @Override
    protected void loadData() {
        Issue issue = InchoateApp.getNewestIssueCache().get(0);
        if (issue == null || issue.categorySection == null || issue.categorySection.size() == 0) {
            Log.d(TAG, "onCreateView: issue " + issue);
            return;
        }
        mSectionList.addAll(issue.categorySection);
        mSectionList.add(0, "This week");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_float_action;
    }

    @Override
    protected <T> T fetchDataFromDBOrNetwork() {
        return null;
    }

    @Override
    protected <T> T initFakeData() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
