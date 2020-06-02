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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.FieldNamingStrategy;
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

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.xuwanjin.inchoate.Utils.getIssue;
import static com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle.setEconomistService;

public class WeeklyFragment extends Fragment {
    public static final String TAG = "WeeklyFragment";
    RecyclerView issueContentRecyclerView;
    private View mSectionHeaderView;
    private View mFooterView;
    private TextView previousEdition;
    private List<Article> mArticlesList;
    private FloatingActionButton mFab;
    private HashMap<String, List<Article>> issueHashMap = new HashMap<>();
    private View mDownloadAudio;
    private View mStreamAudio;
    private TextView issueDate;
    private TextView magazineHeadline;
    private ImageView magazineCover;
    private WeeklyAdapter mWeeklyAdapter;
    private StickHeaderDecoration mStickHeaderDecoration;
    private View view;
    public DownloadService mDownloadService;
    private Issue mIssue = new Issue();
    private static HashMap<String, Issue> sIssueHashMap = new HashMap<>();
    private Disposable mDisposable;
    public static String formatIssueDateStr = NEWEST_ISSUE_DATE;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);
    private LinearLayoutManager mLinearLayoutManager;
    private Handler mHandler = new Handler();
    public IEconomistService mEconomistService;
    public static final int DELAY_TIME = 3000;
    private boolean isSuccess = false;
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
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        initView();
        initOnClickListener();

        mExecutorService.submit(mBindServiceRunnable);

        int sectionToPosition = InchoateApp.getScrollToPosition();
        if (sectionToPosition > 0) {
            int scrollToPosition = Utils.getArticleSumBySection(sectionToPosition - 2);
            mLinearLayoutManager.scrollToPosition(scrollToPosition);
        }

        if (mIssue != null && mIssue.containArticle != null && mIssue.containArticle.size() > 0) {
            updateWeeklyFragmentContent(mIssue);
        } else {
            Issue issue = initFakeData();
            updateWeeklyFragmentContent(issue);
            loadTodayArticleList();
        }

        return view;
    }

    public void updateWeeklyFragmentContent(Issue issue) {
        mWeeklyAdapter = new WeeklyAdapter(getContext(), this);
        issueContentRecyclerView.setAdapter(mWeeklyAdapter);
        mWeeklyAdapter.setHeaderView(mSectionHeaderView);
        mWeeklyAdapter.setFooterView(mFooterView);
        mStickHeaderDecoration = new StickHeaderDecoration(issueContentRecyclerView, getContext());
        issueContentRecyclerView.addItemDecoration(mStickHeaderDecoration);

        updateView(issue);
    }

    private void loadTodayArticleList() {
        mDisposable = Single.create(new SingleOnSubscribe<Issue>() {
            @Override
            public void subscribe(SingleEmitter<Issue> emitter) throws Exception {
                Issue issue = specificIssueByIssueDateAndUrlID();
                mIssue = issue;
                if (issue == null) {
                    return;
                }
                emitter.onSuccess(issue);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Issue>() {
                    @Override
                    public void accept(Issue issue) throws Exception {
                        updateWeeklyFragmentContent(issue);
                        Log.d(TAG, "loadTodayArticleList: issue.containArticle.size: " + issue.containArticle.size());
                    }
                });

    }

    public Issue specificIssueByIssueDateAndUrlID() {
        Issue issue;
        SharedPreferences preferences =
                getContext().getSharedPreferences(Constants.INCHOATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String urlIdString = preferences.getString(Constants.CURRENT_DISPLAY_ISSUE_URL_ID, "");
        if (!urlIdString.equals("")
                && !urlIdString.contains("null")) {
            String[] value = urlIdString.split(",");
            String issueDate = value[0];
            String issueUrlId = value[1];
            issue = loadWholeIssue(issueDate, issueUrlId);
        } else {
            issue = loadWholeIssue(formatIssueDateStr, WEEK_FRAGMENT_QUERY_05_30_URL);
        }
        return issue;
    }

    // 先查看 load 哪一期, 然后是否从数据库, 还是 网络上 load.
    // 通过 issueDate 查看是否存在数据库当中,
    // 通过 urlId 从网络上加载
    public Issue loadWholeIssue(String issueDate, String urlId) {
        // 数据库 (数据库插入不全)---> 网络
        Issue issue = getIssueDataFromDB(issueDate);
        boolean shouldLoadFromNetwork = false;
        if (issue != null) {
            int size = issue.containArticle.size();
            Article lastArticle = issue.containArticle.get(size - 1);
            if (!ArticleCategorySection.OBITUARY.getName().equals(lastArticle.section)) {
                shouldLoadFromNetwork = true;
            }
        } else {
            shouldLoadFromNetwork = true;
        }

        if (shouldLoadFromNetwork) {
            issue = loadDataFromNetwork(urlId);
        }
        return issue;
    }

    public void initView() {
        issueContentRecyclerView = view.findViewById(R.id.issue_content_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        issueContentRecyclerView.setLayoutManager(mLinearLayoutManager);

        // 这种 header 的出现, 他会 inflate 在 RecyclerView 的上面, 这个时候, 画第一个 item 的 header,
        //也会出现在 RecyclerView 的上面, 但是他会出现, HeaderView 的上面
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        mSectionHeaderView = layoutInflater.inflate(R.layout.weekly_section_header, issueContentRecyclerView, false);
        mFooterView = layoutInflater.inflate(R.layout.weekly_footer, issueContentRecyclerView, false);

        mFab = view.findViewById(R.id.issue_category_fab);
        mFab.setFocusable(true);
        mFab.setClickable(true);
        mFab.setVisibility(View.VISIBLE);

        previousEdition = mSectionHeaderView.findViewById(R.id.previous_edition);
        mDownloadAudio = mSectionHeaderView.findViewById(R.id.download_audio);
        mStreamAudio = mSectionHeaderView.findViewById(R.id.stream_audio);
        issueDate = mSectionHeaderView.findViewById(R.id.issue_date);
        magazineCover = mSectionHeaderView.findViewById(R.id.magazine_cover);
        magazineHeadline = mSectionHeaderView.findViewById(R.id.magazine_headline);
    }

    public void updateView(Issue issue) {
        Glide.with(mSectionHeaderView)
                .load(issue.coverImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.magazine_cover)
                .into(magazineCover);
        issueDate.setText(issue.issueDate);
        magazineHeadline.setText(issue.headline);
        mWeeklyAdapter.updateData(issue.containArticle);
    }

    public void initOnClickListener() {
        Runnable mStreamAudioRunnable = new Runnable() {
            @Override
            public void run() {
                List<Article> articleList = mIssue.containArticle;
                InchoateApp.setAudioPlayingArticleListCache(articleList);
                SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                panelState.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                EventBus.getDefault().post(panelState);
                if (mIssue != null) {
                    EconomistPlayerTimberStyle.playWholeIssue(mIssue.containArticle.get(0), mIssue, WEEKLY_PLAYING_SOURCE);
                }
            }
        };

        mStreamAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIssue != null && mIssue.containArticle != null) {
                    mExecutorService.submit(mStreamAudioRunnable);
                }
            }
        });

        mDownloadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIssue != null &&
                        mIssue.issueFormatDate != null
                        && !"".equals(mIssue.issueFormatDate)) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), DownloadService.class);
                    intent.putExtra(PENDING_DOWNLOAD_ISSUE_DATE, mIssue.issueFormatDate);
                    getContext().startService(intent);
                    getContext().bindService(intent, serviceConnection, 0);
                    mHandler.post(mGetDownloadPercentRunnable);
                }
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationToFragment(R.id.navigation_float_action);
            }
        });

        previousEdition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationToFragment(R.id.navigation_previous_edition);
            }
        });
    }

    private void navigationToFragment(int resId) {
        Utils.navigationController(InchoateApp.NAVIGATION_CONTROLLER, resId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private Issue initFakeData() {
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
        InchoateDBHelper helper = new InchoateDBHelper(getContext(), null, null);
        List<Issue> issueList = helper.queryIssueByFormatIssueDate(issueDate);
        if (issueList != null && issueList.size() > 0) {
            issue = issueList.get(0);
        }
        return issue;
    }

    public Issue parseJsonDataFromAsset() {
        Gson gson = new Gson()
                .newBuilder()
                .setFieldNamingStrategy(new FieldNamingStrategy() {
                    @Override
                    public String translateName(Field f) {
                        String name = f.getName();
                        if (name.contains("-")) {
                            return name.replaceAll("-", "");
                        }
                        return name;
                    }
                }) // setFieldNamingPolicy 有什么区别
                .create();

        InputStream jsonStream = getContext().getResources().openRawResource(R.raw.week_fragment_query);
        InputStreamReader reader = new InputStreamReader(jsonStream);
        WeekJson weekJson = gson.fromJson(reader, WeekJson.class);
        final Issue issue = getIssue(weekJson);
        InchoateApp.setNewestIssueCache(issue);
        mIssue = issue;
        Runnable insertDataRunnable = new Runnable() {
            @Override
            public void run() {
                final InchoateDBHelper helper = new InchoateDBHelper(getActivity(), null, null);
                helper.insertWholeData(issue);
            }
        };
        mExecutorService.submit(insertDataRunnable);
        mArticlesList = issue.containArticle;
        return issue;
    }

    public Issue loadDataFromNetwork(String urlId) {
        String wholeUrlId = WEEK_FRAGMENT_COMMON_URL + urlId + TAIL;
        Log.d(TAG, "loadDataFromNetwork: urlId = " + urlId);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(wholeUrlId)
                .build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || !response.isSuccessful()) {
            return null;
        }
        String jsonResult = null;
        try {
            jsonResult = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonResult == null) {
            return null;
        }
        Gson gson = new Gson()
                .newBuilder()
                .setFieldNamingStrategy(new FieldNamingStrategy() {
                    @Override
                    public String translateName(Field f) {
                        String name = f.getName();
                        if (name.contains("-")) {
                            return name.replaceAll("-", "");
                        }
                        return name;
                    }
                }) // setFieldNamingPolicy 有什么区别
                .create();
        WeekJson weekJson = gson.fromJson(jsonResult, WeekJson.class);
        Issue issue = getIssue(weekJson);
        InchoateApp.setNewestIssueCache(issue);
        mArticlesList = issue.containArticle;
        Log.d(TAG, "loadDataFromNetwork: ");
        Runnable mInsertIssueData = new Runnable() {
            @Override
            public void run() {
                InchoateDBHelper helper = new InchoateDBHelper(getContext(), null, null);
                helper.insertWholeData(issue);
            }
        };
        mExecutorService.submit(mInsertIssueData);
        return issue;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isSuccess && mEconomistService != null) {
            EconomistPlayerTimberStyle.unbindToService(getActivity(), economistServiceConnection);
        }
    }
}
