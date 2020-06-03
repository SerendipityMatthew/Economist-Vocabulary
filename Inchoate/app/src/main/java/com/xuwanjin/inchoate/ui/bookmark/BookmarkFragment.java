package com.xuwanjin.inchoate.ui.bookmark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.model.Article;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BookmarkFragment extends Fragment {
    RecyclerView bookmarkRecycleView;
    TextView mTextView;
    private Disposable mDisposable;
    private BookmarkAdapter mBookmarkAdapter;
    static List<Article> sArticleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        bookmarkRecycleView = view.findViewById(R.id.bookmark_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        bookmarkRecycleView.setLayoutManager(gridLayoutManager);
        mTextView = view.findViewById(R.id.bookmark_title);

        if (sArticleList.size() > 0) {
            updateFragmentContent(sArticleList);
        }else {
            loadBookmarkData();
        }
        return view;
    }

    private void loadBookmarkData() {
        mDisposable = Single.create(new SingleOnSubscribe<List<Article>>() {
            @Override
            public void subscribe(SingleEmitter<List<Article>> emitter) throws Exception {
                List<Article> articleList = new ArrayList<>();
                InchoateDBHelper helper = new InchoateDBHelper(getContext(), null, null);
                articleList = helper.queryBookmarkedArticle();
                helper.close();
                sArticleList = articleList;
                emitter.onSuccess(articleList);
            }
        })
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Article>>() {
            @Override
            public void accept(List<Article> articles) throws Exception {
                if (articles.size()> 0){
                    updateFragmentContent(articles);
                }
            }
        });

    }

    private void updateFragmentContent(List<Article> articleList) {
        mBookmarkAdapter = new BookmarkAdapter(getContext(), BookmarkFragment.this);
        bookmarkRecycleView.setAdapter(mBookmarkAdapter);
        bookmarkRecycleView.addItemDecoration(new StickHeaderDecoration(bookmarkRecycleView, getContext()));
        mBookmarkAdapter.updateData(articleList);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
