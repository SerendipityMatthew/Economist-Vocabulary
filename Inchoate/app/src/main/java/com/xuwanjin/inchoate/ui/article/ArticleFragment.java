package com.xuwanjin.inchoate.ui.article;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
    TextView backToWeeklyToolbar;
    ImageView fontSizeToolbar;
    ImageView bookmarkArticleToolbar;
    ImageView articleShareToolbar;
    LinearLayout mLinearLayout;
    View articlePlayBarDivider;
    int count = 0;
    private List<String> collectedVocabularyList = new ArrayList<>();
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
        initData();
        initOnClickListener();

        mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        mArticleContentRV.setLayoutManager(mGridLayoutManager);
        mArticleContentAdapter = new ArticleContentAdapter(getContext(), mParagraphList, view);
        ArticleItemDecoration articleItemDecoration = new ArticleItemDecoration(mArticleContentRV, getContext());
        mArticleContentRV.addItemDecoration(articleItemDecoration);
        mArticleContentRV.setAdapter(mArticleContentAdapter);
        mArticleContentAdapter.setHeaderView(mArticleContentHeaderView);
        mArticleContentAdapter.setFooterView(mArticleContentFooterView);

        initFillCollectedVocabulary();
        processArticleText();
        return view;
    }

    private void initFillCollectedVocabulary() {
        collectedVocabularyList.addAll(InchoateApp.sCollectedVocabularyList);
        Log.d(TAG, "initFillCollectedVocabulary:collectedVocabularyList.size =  " + collectedVocabularyList.size());
    }

    /*
      paragraphList                   VocabularyList
      paragraph 01                    incendiary
      paragraph 02                    hoodlum
      paragraph 03                    babble
      paragraph 04                    incite
      paragraph 05                    inflammatory
      paragraph 06                    magnanimous
      paragraph 07                    retrenchment
      paragraph 08                    flagrant
      paragraph 08                    dissidents
       多对多的关系   flatmap
 */
    @SuppressLint("CheckResult")
    private void processArticleText() {

        List<Paragraph> paragraphList = article.paragraphList;
        List<Paragraph> adapterDataList = new ArrayList<>();
        adapterDataList.addAll(article.paragraphList);
        Log.d(TAG, "processArticleText: paragraphList.size = " + paragraphList.size());
        Flowable.fromIterable(paragraphList)
                .map(new Function<Paragraph, HashMap<Integer, Paragraph>>() {
                    @Override
                    public HashMap<Integer, Paragraph> apply(Paragraph paragraph) throws Exception {
                        return processArticleTextToSpannable(paragraph);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HashMap<Integer, Paragraph>>() {
                    @Override
                    public void accept(HashMap<Integer, Paragraph> integerParagraphHashMap) throws Exception {
                        // 更新 Article 的 ParagraphList .
                        Integer index = integerParagraphHashMap.keySet().iterator().next();
                        adapterDataList.add(index, integerParagraphHashMap.get(index));
                        if (count == adapterDataList.size() - 1) {
                            mArticleContentAdapter.updateData(adapterDataList);
                        }
                    }
                });
    }

    public HashMap<Integer, Paragraph> processArticleTextToSpannable(Paragraph paragraph) {
        String paragraphText = paragraph.paragraph.toString();
        HashMap<Integer, Paragraph> hashMap = new HashMap<>(0);
        HashMap<Integer, CharSequence> collectedVocabularyHashMap = new HashMap<>();
        for (int i = 0; i < collectedVocabularyList.size(); i++) {
            String collectedVocabulary = collectedVocabularyList.get(i);
            if (isSkipVocabulary(collectedVocabulary)) {
                continue;
            }
            // //? ! . , : "  特殊情况
            String collectedVocabularyPattern = " " + collectedVocabularyList.get(i) + " ";
            boolean isExisted = paragraphText.contains(collectedVocabularyPattern);
            // 如果一个段落里多个不认识的单词, 存在 hashmap 里
            if (isExisted) {
                int index = paragraphText.indexOf(collectedVocabulary);
                collectedVocabularyHashMap.put(Integer.valueOf(index), collectedVocabulary);
            }
        }
        SpannableString vocabularySpannable = new SpannableString(paragraphText);
        if (collectedVocabularyHashMap.size() > 0) {
            for (Integer index : collectedVocabularyHashMap.keySet()) {
                CharSequence vocabulary = collectedVocabularyHashMap.get(index);
                vocabularySpannable.setSpan(
                        new BackgroundColorSpan(Color.GREEN),
                        index, index + vocabulary.length(),
                        SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
            }
            paragraph.paragraph = vocabularySpannable;
        }
        hashMap.put(paragraph.theOrderOfParagraph, paragraph);
        return hashMap;
    }

    public boolean isSkipVocabulary(String vocabulary) {
        if (vocabulary.length() == 1
                || vocabulary.length() == 2
                || vocabulary.length() == 3
                || vocabulary.length() == 4
        ) {
            return true;
        }
        //数字类的去掉
        Pattern pattern = Pattern.compile(".*\\\\d+.*");
        Matcher matcher = pattern.matcher(vocabulary);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public void initView() {
        mArticleContentRV = view.findViewById(R.id.article_content_recyclerview);
        backToWeeklyToolbar = view.findViewById(R.id.back_to_weekly_toolbar);
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
                dbHelper.close();
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        };
        backToWeeklyToolbar.setOnClickListener(onClickListener);
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
