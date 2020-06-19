package com.xuwanjin.inchoate.ui.weekly;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.download.DownloadService;
import com.xuwanjin.inchoate.events.SlidingUpControllerEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.ArticleCategorySection;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.week.WeekJson;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;

import static com.xuwanjin.inchoate.Constants.NEWEST_ISSUE_DATE;
import static com.xuwanjin.inchoate.Constants.PENDING_DOWNLOAD_ISSUE_DATE;
import static com.xuwanjin.inchoate.Constants.TAIL;
import static com.xuwanjin.inchoate.Constants.WEEKLY_PLAYING_SOURCE;
import static com.xuwanjin.inchoate.Constants.WEEK_FRAGMENT_COMMON_URL;
import static com.xuwanjin.inchoate.Constants.WEEK_FRAGMENT_QUERY_05_30_URL;

import com.xuwanjin.inchoate.timber_style.IEconomistService;
import com.xuwanjin.inchoate.ui.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.xuwanjin.inchoate.Utils.getIssue;
import static com.xuwanjin.inchoate.model.ArticleCategorySection.OBITUARY;
import static com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle.setEconomistService;

/**
 * @author Matthew Xu
 */
public class WeeklyFragment extends BaseFragment {
    AtomicBoolean mAtomicBoolean = new AtomicBoolean();
    public static final String TAG = "WeeklyFragment";
    RecyclerView mIssueContentRecyclerView;
    private View mSectionHeaderView;
    private TextView mPreviousEdition;
    private FloatingActionButton mFab;
    private View mDownloadAudio;
    private View mStreamAudio;
    private TextView mIssueDate;
    private TextView mMagazineHeadline;
    private ImageView mMagazineCover;
    private WeeklyAdapter mWeeklyAdapter;
    private WeeklyItemDecoration mWeeklyItemDecoration;

    public DownloadService mDownloadService;
    private static Issue sIssueCache = new Issue();

    CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    public static String mFormatIssueDateStr = NEWEST_ISSUE_DATE;
    private static ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private LinearLayoutManager mLinearLayoutManager;
    private Handler mHandler = new Handler();
    public IEconomistService mEconomistService;
    public static final int DELAY_TIME = 3000;
    private boolean isSuccess = false;
    private static boolean isInsertData = false;
    public Runnable mBindServiceRunnable = new Runnable() {
        @Override
        public void run() {
            isSuccess = EconomistPlayerTimberStyle.binToService(getActivity(), economistServiceConnection);
        }
    };

    public Runnable mGetDownloadPercentRunnable = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) {
                return;
            }
            if (mDownloadService != null) {
                float percent = mDownloadService.getDownloadPercent();
                Log.d(TAG, "mGetDownloadPercentRunnable: percent = " + percent);
            }
            mHandler.postDelayed(mGetDownloadPercentRunnable, DELAY_TIME);
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadService = ((DownloadService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDownloadService = null;
        }
    };

    ServiceConnection economistServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mEconomistService = IEconomistService.Stub.asInterface(service);
            setEconomistService(mEconomistService);
            isSuccess = true;
            Log.d(TAG, "onServiceConnected: mEconomistService = " + mEconomistService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            mEconomistService = null;
            setEconomistService(null);
            isSuccess = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mExecutorService.submit(mBindServiceRunnable);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        initWeeklyFragmentView(view);
        initOnClickListener();
    }

    @Override
    public void loadData() {
        /*
            1. 从缓存里获取数据
                存在就显示
                如果不存在,
                        从数据库里获取数据: 1. 根据 xml最新的期刊日期,去数据里获取最新的期刊,
                                         2. 如果没有, 从数据库获取最新的一集,
                                               数据库里什么都没有, 从网络获取
                        从网络上获取数据 ---> 并写入数据库
                        如果以上都没有, 填入假数据

         */
        if (isLoadFromCache()) {
            InchoateApp.setNewestIssueCache(sIssueCache);
            updateWeeklyFragmentContent(sIssueCache);
        } else {
            Issue issue = initFakeData();
            updateWeeklyFragmentContent(issue);
        }
        loadTodayArticleList();
    }

    public boolean isLoadFromCache() {
        return sIssueCache != null && sIssueCache.containArticle != null && sIssueCache.containArticle.size() > 0;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_weekly;
    }

    public void updateWeeklyFragmentContent(Issue issue) {
        updateView(issue);
    }

    private void loadTodayArticleList() {
        Disposable disposable = Single.create(new SingleOnSubscribe<Issue>() {
            @Override
            public void subscribe(SingleEmitter<Issue> emitter) throws Exception {
                Issue issue = fetchDataFromDBOrNetwork();
                sIssueCache = issue;
                if (issue == null) {
                    return;
                }
                InchoateApp.setNewestIssueCache(issue);
                emitter.onSuccess(issue);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Issue>() {
                    @Override
                    public void accept(Issue issue) throws Exception {
                        sIssueCache = issue;
                        Log.d(TAG, "loadTodayArticleList: issue.containArticle.size: " + issue.containArticle.size());
                        updateWeeklyFragmentContent(sIssueCache);
                        updateDatabase(sIssueCache);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Issue issue = initFakeData();
                        updateWeeklyFragmentContent(issue);
                        Log.d(TAG, "loadTodayArticleList: accept: throwable = " + throwable);
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    public void updateDatabase(Issue issue) {
        Runnable mInsertIssueData = new Runnable() {
            @Override
            public void run() {
                isInsertData = true;
                final Disposable disposable;
                Log.d(TAG, "mInsertIssueData: run: ");
                InchoateDBHelper helper = InchoateDBHelper.getInstance(getContext());
                disposable = helper.insertWholeData(issue);
                mCompositeDisposable.add(disposable);
//                helper.close();
                isInsertData = false;
            }
        };
        if (!isInsertData) {
            mExecutorService.schedule(mInsertIssueData, 10, TimeUnit.SECONDS);
        }
        // 延迟插入数据, 防止线程竞争打开数据库的问题.
    }

    @Override
    public Issue fetchDataFromDBOrNetwork() {
        Issue issue;
        getCurrentIssueDateFromPreference();
        String urlIdString = getCurrentIssueDateFromPreference();
        if (!urlIdString.equals("")
                && !urlIdString.contains("null")) {
            String[] value = urlIdString.split(",");
            String issueDate = value[0];
            String issueUrlId = value[1];
            Log.d(TAG, "fetchDataFromDBOrNetwork: issueDate = " + issueDate);
            Log.d(TAG, "fetchDataFromDBOrNetwork: issueUrlId = " + issueUrlId);
            issue = loadWholeIssue(issueDate, issueUrlId);
        } else {
            issue = loadDataFromNetwork(WEEK_FRAGMENT_QUERY_05_30_URL);
        }
        return issue;
    }

    private String getCurrentIssueDateFromPreference() {
        SharedPreferences preferences =
                getContext().getSharedPreferences(Constants.INCHOATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String urlIdString = preferences.getString(Constants.CURRENT_DISPLAY_ISSUE_URL_ID, "");
        return urlIdString;
    }

    /**
        先查看 load 哪一期, 然后是否从数据库, 还是 网络上 load.
        通过 issueDate 查看是否存在数据库当中,
        通过 urlId 从网络上加载
     */
    public Issue loadWholeIssue(String issueDate, String urlId) {
        // 数据库 (数据库插入不全)---> 网络
        Issue issue = getIssueDataFromDB(issueDate);
        Log.d(TAG, "loadWholeIssue: issue = " + issue);
        boolean shouldLoadFromNetwork = false;
        if (issue != null) {
            List<Article> articleList = issue.containArticle;
            // 所有的文章都没被插入
            if (articleList == null || articleList.size() == 0) {
                Log.d(TAG, "loadWholeIssue: if: if: ");
                shouldLoadFromNetwork = true;
            } else {
                Log.d(TAG, "loadWholeIssue: if: else: ");
                // 可能插入了部分文章
                int size = articleList.size();
                Article lastArticle = articleList.get(size - 1);
                // 最后一篇文章不是 OBITUARY,  没有完全插入接入
                if (!OBITUARY.getName().equals(lastArticle.section)) {
                    shouldLoadFromNetwork = true;
                }
            }

        } else {
//            issue = getNewestIssueDataFromDB();
//            Log.d(TAG, "loadWholeIssue: issue = " + issue);
//            if (issue == null || issue.containArticle == null || issue.containArticle.size() == 0){
            shouldLoadFromNetwork = true;
//            }
        }

        if (shouldLoadFromNetwork) {
            issue = loadDataFromNetwork(urlId);
        }
        return issue;
    }


    public void initWeeklyFragmentView(View view) {
        mIssueContentRecyclerView = view.findViewById(R.id.issue_content_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mIssueContentRecyclerView.setLayoutManager(mLinearLayoutManager);

        /*
            这种 header 的出现, 他会 inflate 在 RecyclerView 的上面, 这个时候, 画第一个 item 的 header,
            也会出现在 RecyclerView 的上面, 但是他会出现, HeaderView 的上面
         */
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        mSectionHeaderView = layoutInflater.inflate(R.layout.weekly_section_header, mIssueContentRecyclerView, false);
        View mFooterView = layoutInflater.inflate(R.layout.weekly_footer, mIssueContentRecyclerView, false);

        mFab = view.findViewById(R.id.issue_category_fab);
        mFab.setFocusable(true);
        mFab.setClickable(true);
        mFab.setVisibility(View.VISIBLE);

        mPreviousEdition = mSectionHeaderView.findViewById(R.id.previous_edition);
        mDownloadAudio = mSectionHeaderView.findViewById(R.id.download_audio);
        mStreamAudio = mSectionHeaderView.findViewById(R.id.stream_audio);
        mIssueDate = mSectionHeaderView.findViewById(R.id.issue_date);
        mMagazineCover = mSectionHeaderView.findViewById(R.id.magazine_cover);
        mMagazineHeadline = mSectionHeaderView.findViewById(R.id.magazine_headline);

        mWeeklyAdapter = new WeeklyAdapter(getContext());
        mIssueContentRecyclerView.setAdapter(mWeeklyAdapter);
        mWeeklyAdapter.setHeaderView(mSectionHeaderView);
        mWeeklyAdapter.setFooterView(mFooterView);
        mWeeklyItemDecoration = new WeeklyItemDecoration(getContext(), mIssueContentRecyclerView);
        mIssueContentRecyclerView.addItemDecoration(mWeeklyItemDecoration);

        int sectionToPosition = InchoateApp.getScrollToPosition();
        Log.d(TAG, "onCreateView: sectionToPosition = " + sectionToPosition);
        if (sectionToPosition > 0) {
            int scrollToPosition = Utils.getArticleSumBySection(sectionToPosition - 2, sIssueCache);
            Log.d(TAG, "onCreateView: scrollToPosition = " + scrollToPosition);
            if (scrollToPosition > 0) {
                mLinearLayoutManager.scrollToPosition(scrollToPosition);
            }
        }
    }

    public void updateView(Issue issue) {
        Glide.with(mSectionHeaderView)
                .load(issue.coverImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.the_economist_cover_placeholder)
                .into(mMagazineCover);
        mIssueDate.setText(issue.issueDate);
        mMagazineHeadline.setText(issue.headline);
        mWeeklyAdapter.updateData(issue.containArticle);
    }

    public void initOnClickListener() {
        Runnable mStreamAudioRunnable = new Runnable() {
            @Override
            public void run() {
                List<Article> articleList = sIssueCache.containArticle;
                InchoateApp.setsAudioPlayingArticleListCache(articleList);
                SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                panelState.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                EventBus.getDefault().post(panelState);
                if (sIssueCache != null) {
                    EconomistPlayerTimberStyle.playWholeIssue(sIssueCache.containArticle.get(0), sIssueCache, WEEKLY_PLAYING_SOURCE);
                }
            }
        };

        mStreamAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sIssueCache != null && sIssueCache.containArticle != null) {
                    mExecutorService.submit(mStreamAudioRunnable);
                }
            }
        });

        mDownloadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sIssueCache != null &&
                        sIssueCache.issueFormatDate != null
                        && !"".equals(sIssueCache.issueFormatDate)) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), DownloadService.class);
                    intent.putExtra(PENDING_DOWNLOAD_ISSUE_DATE, sIssueCache.issueFormatDate);
                    getContext().startService(intent);
                    getContext().bindService(intent, serviceConnection, 0);
                    mHandler.post(mGetDownloadPercentRunnable);
                }
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> sectionList = new ArrayList<>(sIssueCache.categorySection);
                getActivity().getIntent().putStringArrayListExtra("issue_section", sectionList);
                navigationToFragment(R.id.navigation_float_action);
            }
        });

        mPreviousEdition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationToFragment(R.id.navigation_previous_edition);
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected Issue initFakeData() {
        Issue issue = new Issue();
        List<Article> articleList = new ArrayList<>();
        for (int i = 0; i < 82; i++) {
            Article article = new Article();
            article.section = "Matthew";
            article.headline = "Matthew = " + ArticleCategorySection.BRIEFING;
            articleList.add(article);
        }
        issue.coverImageUrl = "";
        issue.issueDate = "";
        issue.issueUrl = "";
        issue.issueFormatDate = "";
        issue.containArticle = articleList;

        return issue;
    }

    private Issue getIssueDataFromDB(String issueDate) {
        Issue issue = null;
        InchoateDBHelper helper = InchoateDBHelper.getInstance(getContext());
        List<Issue> issueList = helper.queryIssueByFormatIssueDate(issueDate);
        if (issueList != null && issueList.size() > 0) {
            issue = issueList.get(0);
        }
//        helper.close();
        return issue;
    }

    public Issue loadDataFromNetwork(String urlId) {
        String wholeUrl = WEEK_FRAGMENT_COMMON_URL + urlId + TAIL;
        Log.d(TAG, "loadDataFromNetwork: urlId = " + urlId);
        String jsonResult = fetchJsonFromServer(wholeUrl);
        if (jsonResult == null) {
            return null;
        }
        Gson gson = getGsonInstance();
        WeekJson weekJson = gson.fromJson(jsonResult, WeekJson.class);
        Issue issue = getIssue(weekJson);
        InchoateApp.setNewestIssueCache(issue);
        return issue;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable.isDisposed()) {
            mExecutorService.isShutdown();
            mCompositeDisposable.dispose();
        }
        if (isSuccess && mEconomistService != null) {
            EconomistPlayerTimberStyle.unbindToService(getActivity(), economistServiceConnection);
        }
    }
}
