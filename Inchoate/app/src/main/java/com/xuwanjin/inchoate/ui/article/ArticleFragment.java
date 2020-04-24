package com.xuwanjin.inchoate.ui.article;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xuwanjin.inchoate.InchoateActivity;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Paragraph;

import java.util.List;

import static com.xuwanjin.inchoate.Utils.getDurationFormat;

public class ArticleFragment extends Fragment {
    RecyclerView mArticleContentRV;
    public ArticleContentAdapter mArticleContentAdapter;
    public List<Paragraph> mParagraphList;
    public GridLayoutManager mGridLayoutManager;
    public View mArticleContentHeaderView;
    TextView sectionAndDate;
    ImageView play;
    TextView duration;
    TextView articleTitle;
    ImageView articleCoverImage;
    Article article;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        article = InchoateApplication.getDisplayArticleCache();
        if (article != null) {
            mParagraphList = article.paragraphList;
        }
        view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        initView();
        mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        mArticleContentRV.setLayoutManager(mGridLayoutManager);
        mArticleContentAdapter = new ArticleContentAdapter(getContext(), mParagraphList, view);
        ArticleItemDecoration articleItemDecoration = new ArticleItemDecoration(mArticleContentRV, getContext());
        mArticleContentRV.addItemDecoration(articleItemDecoration);
        mArticleContentRV.setAdapter(mArticleContentAdapter);
        mArticleContentAdapter.setHeaderView(mArticleContentHeaderView);
        initData();
        initOnClickListener();
        return view;
    }

    public void initView() {
        mArticleContentRV = view.findViewById(R.id.article_content_recyclerview);
        mArticleContentHeaderView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_article_header_view, mArticleContentRV, false);
        duration = mArticleContentHeaderView.findViewById(R.id.duration);
        play = mArticleContentHeaderView.findViewById(R.id.play);
        articleCoverImage = mArticleContentHeaderView.findViewById(R.id.article_cover_image);
        sectionAndDate = mArticleContentHeaderView.findViewById(R.id.section_and_date);
        articleTitle = mArticleContentHeaderView.findViewById(R.id.article_title);
    }

    public void initData() {
        articleTitle.setText(article.title);
        sectionAndDate.setText(article.section + "  |  " + article.date);
        duration.setText(getDurationFormat(article.audioDuration));
        Glide.with(getContext()).load(article.imageUrl).into(articleCoverImage);

    }

    public void initOnClickListener() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InchoateApplication.setDisplayArticleCache(article);
//                Utils.navigationController(InchoateApplication.NAVIGATION_CONTROLLER,R.id.navigation_audio_playing);
                ((InchoateActivity) getActivity()).slidingUpPanelLayout.setPanelHeight(400);

            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
