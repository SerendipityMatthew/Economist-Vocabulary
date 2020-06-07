package com.xuwanjin.inchoate.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    private View mRootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutResId = getLayoutResId();
        mRootView = inflater.inflate(layoutResId, container, false);
        initView(mRootView);
        loadData();
        return mRootView;
    }

    protected abstract void initView(View view);

    protected abstract void loadData();

    protected abstract int getLayoutResId();
}
