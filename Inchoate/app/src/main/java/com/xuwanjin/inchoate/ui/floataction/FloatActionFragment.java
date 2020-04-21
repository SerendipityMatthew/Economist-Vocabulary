package com.xuwanjin.inchoate.ui.floataction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.InchoateActivity;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;

import java.util.List;

public class FloatActionFragment extends Fragment {
    private View mHeaderSectionView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_float_action, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.float_action_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<String> sectionList = InchoateApplication.getNewestIssueCache().get(0).categorySection;
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
