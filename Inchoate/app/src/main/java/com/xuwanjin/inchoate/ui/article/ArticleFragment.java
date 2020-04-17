package com.xuwanjin.inchoate.ui.article;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Paragraph;

import java.util.List;

public class ArticleFragment extends Fragment {
    RecyclerView articleContentRV;
    public ArticleContentAdapter mArticleContentAdapter;
    public List<Paragraph> mParagraphList;
    public GridLayoutManager mGridLayoutManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        articleContentRV = view.findViewById(R.id.article_content_recyclerview);
        mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        articleContentRV.setLayoutManager(mGridLayoutManager);
        mArticleContentAdapter = new ArticleContentAdapter(getContext(), mParagraphList);
        articleContentRV.setAdapter(mArticleContentAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
