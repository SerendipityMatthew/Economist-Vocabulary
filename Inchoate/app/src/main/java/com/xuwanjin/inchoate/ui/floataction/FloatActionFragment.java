package com.xuwanjin.inchoate.ui.floataction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Issue;

import java.util.ArrayList;
import java.util.List;

public class FloatActionFragment extends Fragment {
    public static final String TAG = "FloatActionFragment";
    private View mHeaderSectionView;
    private List<String> sectionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_float_action, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.float_action_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        Issue issue = InchoateApp.getNewestIssueCache().get(0);
        if (issue == null || issue.categorySection == null || issue.categorySection.size() == 0) {
            Log.d(TAG, "onCreateView: issue " + issue.categorySection);
            return null;
        }
        sectionList.addAll(issue.categorySection);
        sectionList.add(0, "This week");
        IssueCategoryAdapter categoryAdapter = new IssueCategoryAdapter(
                getContext(), sectionList);
        mHeaderSectionView = LayoutInflater.from(getContext()).inflate(R.layout.float_action_header, recyclerView, false);
        categoryAdapter.setHeaderView(mHeaderSectionView);
        recyclerView.addItemDecoration(new IssueCategoryStickHeaderDecoration(recyclerView, getContext()));
        recyclerView.setAdapter(categoryAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
