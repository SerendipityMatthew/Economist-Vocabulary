package com.xuwanjin.inchoate.ui.article;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.events.SlidingUpControllerEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;
import com.xuwanjin.inchoate.timber_style.IEconomistService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.xuwanjin.inchoate.Utils.getDurationFormat;

public class ArticleFragment extends Fragment {
    public static final String TAG = "ArticleFragment";
    RecyclerView mArticleContentRV;
    public ArticleContentAdapter mArticleContentAdapter;
    public List<Paragraph> mParagraphList;
    public GridLayoutManager mGridLayoutManager;
    public View mArticleContentHeaderView;
    public View mArticleContentFooterView;
    TextView sectionAndDate;
    ImageView play;
    TextView duration;
    TextView articleTitle;
    TextView articleFlyTitle;
    TextView articleRubric;
    ImageView articleCoverImage;
    Article article;
    View view;
    ImageView backToWeeklyToolbar;
    TextView weeklyToolbar;
    ImageView fontSizeToolbar;
    ImageView bookmarkArticleToolbar;
    ImageView articleShareToolbar;
    LinearLayout mLinearLayout;
    View articlePlayBarDivider;
    private IEconomistService mEconomistService;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mEconomistService = IEconomistService.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            mEconomistService = null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        article = InchoateApp.getDisplayArticleCache();
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
        mArticleContentAdapter.setFooterView(mArticleContentFooterView);
        initData();
        initOnClickListener();
        return view;
    }

    public void initView() {
        mArticleContentRV = view.findViewById(R.id.article_content_recyclerview);
        backToWeeklyToolbar = view.findViewById(R.id.back_to_weekly_toolbar);
        weeklyToolbar = view.findViewById(R.id.weekly_toolbar);
        fontSizeToolbar = view.findViewById(R.id.font_size_toolbar);
        bookmarkArticleToolbar = view.findViewById(R.id.bookmark_article_toolbar);
        articleShareToolbar = view.findViewById(R.id.article_share_toolbar);

        mArticleContentHeaderView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_article_header_view, mArticleContentRV, false);
        mArticleContentFooterView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_article_detail_footer, mArticleContentRV, false);

        duration = mArticleContentHeaderView.findViewById(R.id.duration);
        play = mArticleContentHeaderView.findViewById(R.id.play);
        articleCoverImage = mArticleContentHeaderView.findViewById(R.id.article_cover_image);
        sectionAndDate = mArticleContentHeaderView.findViewById(R.id.section_and_date);
        articleTitle = mArticleContentHeaderView.findViewById(R.id.article_title);
        articleFlyTitle = mArticleContentHeaderView.findViewById(R.id.article_fly_title);
        articleRubric = mArticleContentHeaderView.findViewById(R.id.article_rubric);

        mLinearLayout = mArticleContentHeaderView.findViewById(R.id.article_play_bar);
        articlePlayBarDivider = mArticleContentHeaderView.findViewById(R.id.article_play_bar_divider);
    }

    public void initData() {
        articleTitle.setText(article.title);
        articleFlyTitle.setText(article.flyTitle);
        articleRubric.setText(article.articleRubric);
        String sectionAndDateStr = article.section + "  |  " + article.date;
        SpannableString sectionSpannable = new SpannableString(sectionAndDateStr);
        sectionSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, article.section.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        sectionAndDate.setText(sectionSpannable);

        duration.setText(getDurationFormat(article.audioDuration));
        Glide.with(getContext())
                .load(article.mainArticleImage)
                .into(articleCoverImage);
        Log.d(TAG, "initData: article = " + article);

        if (article != null) {
            Glide.with(getActivity())
                    .load(article.isBookmark ? R.mipmap.bookmark_black : R.mipmap.bookmark_white)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.bookmark_white)
                    .into(bookmarkArticleToolbar);
        }

        if (article != null
                && (article.audioUrl == null
                || article.audioUrl.trim().equals("")
                || article.audioUrl.equalsIgnoreCase("null"))) {
            mLinearLayout.setVisibility(View.GONE);
            articlePlayBarDivider.setVisibility(View.GONE);
        }
    }

    public void initOnClickListener() {
        Runnable playArticleRunnable = new Runnable() {
            @Override
            public void run() {
                InchoateApp.setDisplayArticleCache(article);
                ArrayList<Article> arrayList = new ArrayList<>();
                arrayList.add(article);
                InchoateApp.setAudioPlayingArticleListCache(arrayList);
                SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                panelState.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                EventBus.getDefault().post(panelState);
                EconomistPlayerTimberStyle.playWholeIssueByIssueDate(article, article.date);
            }
        };

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutorService.submit(playArticleRunnable);
            }
        });

        bookmarkArticleToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (article.isBookmark) {
                    article.isBookmark = false;
                } else {
                    article.isBookmark = true;
                }
                Glide.with(getActivity())
                        .load(article.isBookmark ? R.mipmap.bookmark_black : R.mipmap.bookmark_white)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.bookmark_white)
                        .into(bookmarkArticleToolbar);
                InchoateDBHelper dbHelper = new InchoateDBHelper(getActivity(), null, null);
                dbHelper.setBookmarkStatus(article, article.isBookmark);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EconomistPlayerTimberStyle.unbindToService(getActivity(), mConnection);
    }
}
