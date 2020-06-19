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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.events.SlidingUpControllerEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;
import com.xuwanjin.inchoate.timber_style.IEconomistService;
import com.xuwanjin.inchoate.ui.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.xuwanjin.inchoate.Utils.getDurationFormat;

/**
 * @author Matthew Xu
 */
public class ArticleFragment extends BaseFragment<ArticleContentAdapter, ArticleItemDecoration, Object> {
    public static final String TAG = "ArticleFragment";
    public static final String DIGITAL_PATTERN = "\".*\\\\\\\\d+.*\"";
    public List<Paragraph> mParagraphList;
    public GridLayoutManager mGridLayoutManager;
    public View mArticleContentHeaderView;
    public View mArticleContentFooterView;
    TextView mSectionAndDate;
    ImageView mPlay;
    TextView mDuration;
    TextView mArticleTitle;
    TextView mArticleFlyTitle;
    TextView mArticleRubric;
    ImageView mArticleCoverImage;
    Article mArticle;
    TextView mBackToWeeklyToolbar;
    ImageView mFontSizeToolbar;
    ImageView mBookmarkArticleToolbar;
    ImageView mArticleShareToolbar;
    LinearLayout mLinearLayout;
    View mArticlePlayBarDivider;
    int mCount = 0;
    private View mView;
    private List<String> mCollectedVocabularyList = new ArrayList<>();
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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        initArticleFragment(view);
        initOnClickListener();
    }

    private void initArticleFragment(View view) {
        mRecyclerView = view.findViewById(R.id.article_content_recyclerview);
        mBackToWeeklyToolbar = view.findViewById(R.id.back_to_weekly_toolbar);
        mFontSizeToolbar = view.findViewById(R.id.font_size_toolbar);
        mBookmarkArticleToolbar = view.findViewById(R.id.bookmark_article_toolbar);
        mArticleShareToolbar = view.findViewById(R.id.article_share_toolbar);

        mArticleContentHeaderView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_article_header_view, mRecyclerView, false);
        mArticleContentFooterView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_article_detail_footer, mRecyclerView, false);

        mDuration = mArticleContentHeaderView.findViewById(R.id.duration);
        mPlay = mArticleContentHeaderView.findViewById(R.id.play);
        mArticleCoverImage = mArticleContentHeaderView.findViewById(R.id.article_cover_image);
        mSectionAndDate = mArticleContentHeaderView.findViewById(R.id.section_and_date);
        mArticleTitle = mArticleContentHeaderView.findViewById(R.id.article_title);
        mArticleFlyTitle = mArticleContentHeaderView.findViewById(R.id.article_fly_title);
        mArticleRubric = mArticleContentHeaderView.findViewById(R.id.article_rubric);

        mLinearLayout = mArticleContentHeaderView.findViewById(R.id.article_play_bar);
        mArticlePlayBarDivider = mArticleContentHeaderView.findViewById(R.id.article_play_bar_divider);
        mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mView = view;
    }

    @Override
    protected void loadData() {
        initData();
        setArticleAdapter();
        initFillCollectedVocabulary();
        processArticleTextWithFlatMap();
    }

    private void setArticleAdapter() {
        mBaseAdapter = new ArticleContentAdapter(getContext(), mParagraphList);
        mRecyclerView.setAdapter(mBaseAdapter);
        mBaseAdapter.setHeaderView(mArticleContentHeaderView);
        mBaseAdapter.setFooterView(mArticleContentFooterView);
        mBaseAdapter.setArticle(mArticle);
        mBaseItemDecoration = new ArticleItemDecoration(getContext(), mRecyclerView);
        mRecyclerView.addItemDecoration(mBaseItemDecoration);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_article_detail;
    }

    @Override
    protected Object fetchDataFromDBOrNetwork() {
        return null;
    }

    @Override
    protected Object initFakeData() {
        return null;
    }

    private void initFillCollectedVocabulary() {
        mCollectedVocabularyList.addAll(InchoateApp.sCollectedVocabularyList);
    }

    /**
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
       多对多的关系   flatmap,
 */
    @SuppressLint("CheckResult")
    private void processArticleText() {

        List<Paragraph> paragraphList = mArticle.paragraphList;
        List<Paragraph> adapterDataList = new ArrayList<>();
        adapterDataList.addAll(mArticle.paragraphList);
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
                        if (mCount == adapterDataList.size() - 1) {
                            mBaseAdapter.updateData(adapterDataList);
                        }
                        mCount++;
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void processArticleTextWithFlatMap() {
        List<Paragraph> paragraphList = mArticle.paragraphList;
        final List<Paragraph> list = paragraphList;
        Function<String, List<Paragraph>> func = new Function<String, List<Paragraph>>() {
            @Override
            public List<Paragraph> apply(String t1) {
                return list;
            }
        };
        BiFunction<String, Paragraph, HashMap<Integer, Paragraph>> resFunc = new BiFunction<String, Paragraph, HashMap<Integer, Paragraph>>() {
            @Override
            public HashMap<Integer, Paragraph> apply(String vocabulary, Paragraph paragraph) {
                // 一段文本 和一个单词  处理应该返回一个 SpannableString, 接着下一次, 返回利用这个结果返回接着处理下一个单词, 再次生成新的 SpannableString
                return processArticleTextToSpannableForFlatMap(paragraph, vocabulary);
            }
        };
//       key: orderOfParagraph  value: SpannableString
        /*
        switch to rxjava filter operator
        List<String> collectedList = new ArrayList<>();

        for (String vocabulary : mCollectedVocabularyList) {
            if (!isSkipVocabulary(vocabulary)) {
                collectedList.add(vocabulary);
            }
        }
        */

        Observable.fromIterable(mCollectedVocabularyList)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String vocabulary) throws Exception {
                        return !isSkipVocabulary(vocabulary);
                    }
                })
                .flatMapIterable(func, resFunc)
                .subscribeOn(Schedulers.computation())
                .delay(10, TimeUnit.SECONDS)
                .subscribe();

    }

    public HashMap<Integer, Paragraph> processArticleTextToSpannableForFlatMap(Paragraph paragraph, String collectedVocabulary) {
        String paragraphText = paragraph.paragraph.toString();
        HashMap<Integer, Paragraph> hashMap = new HashMap<>(0);

        // //? ! . , : "  特殊情况
        String collectedVocabularyPattern = " " + collectedVocabulary + " ";
        boolean isExisted = paragraphText.contains(collectedVocabularyPattern);
        // 如果一个段落里多个不认识的单词, 存在 hashmap 里, 段落的顺序为 key 值, value为段落值
        SpannableString vocabularySpannable = new SpannableString(paragraph.paragraph);
        if (isExisted) {
            int index = 0;
            index = paragraphText.indexOf(collectedVocabulary);
            vocabularySpannable.setSpan(
                    new BackgroundColorSpan(Color.GREEN),
                    index, index + collectedVocabulary.length(),
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        }
        paragraph.paragraph = vocabularySpannable;
        hashMap.put(paragraph.theOrderOfParagraph, paragraph);
        return hashMap;
    }


    public HashMap<Integer, Paragraph> processArticleTextToSpannable(Paragraph paragraph) {
        String paragraphText = paragraph.paragraph.toString();
        HashMap<Integer, Paragraph> hashMap = new HashMap<>(0);
        HashMap<Integer, CharSequence> collectedVocabularyHashMap = new HashMap<>();
        for (int i = 0; i < mCollectedVocabularyList.size(); i++) {
            String collectedVocabulary = mCollectedVocabularyList.get(i);
            if (isSkipVocabulary(collectedVocabulary)) {
                continue;
            }
            // //? ! . , : "  特殊情况
            String collectedVocabularyPattern = " " + mCollectedVocabularyList.get(i) + " ";
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
        Pattern pattern = Pattern.compile(DIGITAL_PATTERN);
        Matcher matcher = pattern.matcher(vocabulary);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public void initData() {
        mArticle = InchoateApp.getDisplayArticleCache();
        if (mArticle != null) {
            mParagraphList = mArticle.paragraphList;
        }

        mArticleTitle.setText(mArticle.title);
        mArticleFlyTitle.setText(mArticle.flyTitle);
        mArticleRubric.setText(mArticle.articleRubric);
        String sectionAndDateStr = mArticle.section + "  |  " + mArticle.date;
        SpannableString sectionSpannable = new SpannableString(sectionAndDateStr);
        sectionSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, mArticle.section.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mSectionAndDate.setText(sectionSpannable);

        mDuration.setText(getDurationFormat(mArticle.audioDuration));
        Glide.with(getContext())
                .load(mArticle.mainArticleImage)
                .placeholder(R.mipmap.article_cover_placeholder)
                .into(mArticleCoverImage);
        Log.d(TAG, "initData: article = " + mArticle);

        if (mArticle != null) {
            Glide.with(getActivity())
                    .load(mArticle.isBookmark ? R.mipmap.bookmark_black : R.mipmap.bookmark_white)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.bookmark_white)
                    .into(mBookmarkArticleToolbar);
        }

        if (mArticle != null
                && (mArticle.audioUrl == null
                || mArticle.audioUrl.trim().equals("")
                || mArticle.audioUrl.equalsIgnoreCase("null"))) {
            mLinearLayout.setVisibility(View.GONE);
            mArticlePlayBarDivider.setVisibility(View.GONE);
        }
    }

    public void initOnClickListener() {
        Runnable playArticleRunnable = new Runnable() {
            @Override
            public void run() {
                InchoateApp.setDisplayArticleCache(mArticle);
                ArrayList<Article> arrayList = new ArrayList<>();
                arrayList.add(mArticle);
                InchoateApp.setsAudioPlayingArticleListCache(arrayList);
                SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                panelState.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                EventBus.getDefault().post(panelState);
                EconomistPlayerTimberStyle.playWholeIssueByIssueDate(mArticle, mArticle.date);
            }
        };

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutorService.submit(playArticleRunnable);
            }
        });

        mBookmarkArticleToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArticle.isBookmark) {
                    mArticle.isBookmark = false;
                } else {
                    mArticle.isBookmark = true;
                }
                Glide.with(getActivity())
                        .load(mArticle.isBookmark ? R.mipmap.bookmark_black : R.mipmap.bookmark_white)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.bookmark_white)
                        .into(mBookmarkArticleToolbar);

                InchoateDBHelper dbHelper = InchoateDBHelper.getInstance(getContext());
                dbHelper.setBookmarkStatus(mArticle, mArticle.isBookmark);
//                dbHelper.close();
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
        mBackToWeeklyToolbar.setOnClickListener(onClickListener);
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
