package com.xuwanjin.inchoate.ui.weekly;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.InchoateActivity;
import com.xuwanjin.inchoate.InchoateApplication;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.database.dao.InchoateDBHelper;
import com.xuwanjin.inchoate.download.DownloadService;
import com.xuwanjin.inchoate.events.SlidingUpControllerEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.ArticleCategorySection;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.week.WeekFragment;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;

import static com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle.mEconomistService;

import com.xuwanjin.inchoate.timber_style.EconomistServiceTimberStyle;
import com.xuwanjin.inchoate.timber_style.IEconomistService;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.xuwanjin.inchoate.Utils.getIssue;

public class WeeklyFragment extends Fragment {
    public static final String TAG = "WeekFragment";
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
    private Issue mIssue;
    private Disposable mDisposable;
    public static String issueDateStr = "May 23rd 2020";
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadService = ((DownloadService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    public static final int FETCH_DATA_AND_NOTIFY_MSG = 1000;


    ServiceConnection economistServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mEconomistService = IEconomistService.Stub.asInterface(service);
            Log.d(TAG, "onServiceConnected: mEconomistService = " + mEconomistService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        initView();
        initOnClickListener();
        mIssue = initFakeData();


        EconomistPlayerTimberStyle.binToService(getActivity(), economistServiceConnection);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        issueContentRecyclerView.setLayoutManager(linearLayoutManager);
        int sectionToPosition = InchoateApplication.getScrollToPosition();
        if (sectionToPosition > 0) {
            int scrollToPosition = Utils.getArticleSumBySection(sectionToPosition - 2);
            linearLayoutManager.scrollToPosition(scrollToPosition);
        }

        mWeeklyAdapter = new WeeklyAdapter(getContext(), this);
        issueContentRecyclerView.setAdapter(mWeeklyAdapter);
        mWeeklyAdapter.setHeaderView(mSectionHeaderView);
        mWeeklyAdapter.setFooterView(mFooterView);

        updateView(mIssue);
        loadTodayArticleList();

        mStickHeaderDecoration = new StickHeaderDecoration(issueContentRecyclerView, getContext());
        issueContentRecyclerView.addItemDecoration(mStickHeaderDecoration);

        return view;
    }

    private void loadTodayArticleList() {
        mDisposable = Single.create(new SingleOnSubscribe<Issue>() {
            @Override
            public void subscribe(SingleEmitter<Issue> emitter) throws Exception {
                Issue issue = initData();
                emitter.onSuccess(issue);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Issue>() {
                    @Override
                    public void accept(Issue issue) throws Exception {
                        updateView(issue);
                        Log.d(TAG, "accept: updateView = " + issue.coverImageUrl);
                    }
                });
    }

    public void initView() {
        issueContentRecyclerView = view.findViewById(R.id.issue_content_recyclerView);

        // 这种 header 的出现, 他会 inflate 在 RecyclerView 的上面, 这个时候, 画第一个 item 的 header,
        //也会出现在 RecyclerView 的上面, 但是他会出现, HeaderView 的上面
        mSectionHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.weekly_section_header, issueContentRecyclerView, false);
        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.weekly_footer, issueContentRecyclerView, false);

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
        mStreamAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InchoateApplication.setAudioPlayingArticleListCache(InchoateApplication.getNewestIssueCache().get(0).containArticle);
                        SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                        panelState.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                        EventBus.getDefault().post(panelState);
                        if (mIssue != null) {
                            EconomistPlayerTimberStyle.playWholeIssue(mIssue.containArticle.get(0), mIssue, Constants.WEEKLY_PLAYING_SOURCE);
                        }
                    }
                }).start();

            }
        });
        mDownloadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), DownloadService.class);
                Issue issue = null;

                try {
                    issue = mIssue.clone();
                    for (Article article : issue.containArticle) {
                        article.paragraphList = null;
                    }
                    intent.putExtra(Constants.DOWNLOAD_ISSUE, issue);
                    getContext().startService(intent);
                    getContext().bindService(new Intent().setClass(getContext(), DownloadService.class), serviceConnection, 0);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                if (mDownloadService != null) {
                                    mDownloadService.getDownloadPercent();
                                } else {
                                    break;
                                }
                            }
                        }
                    }).start();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        });


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigationController(InchoateApplication.NAVIGATION_CONTROLLER, R.id.navigation_float_action);
            }
        });

        previousEdition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: previousEdition = " + previousEdition);
                Utils.navigationController(InchoateApplication.NAVIGATION_CONTROLLER, R.id.navigation_previous_edition);
            }
        });
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
            article.summary = "heeeeeeeeee" + i;
            article.section = "Matthew, helllo";
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

    private Issue initData() {
        List<Article> articles = new ArrayList<>();
        List<Issue> issueList = InchoateApplication.getNewestIssueCache();
        Issue issue = new Issue();
        if (issueList != null && issueList.size() > 0) {
            InchoateDBHelper helper = new InchoateDBHelper(getContext(), null, null);
            issue = helper.queryIssueByIssueDate(issueDateStr).get(0);
            Log.d(TAG, "onResponse: use the cache ");
        } else {
            for (int i = 0; i < 82; i++) {
                Article article = new Article();
                article.section = "Matthew";
                article.headline = "Matthew = " + ArticleCategorySection.BRIEFING;
                articles.add(article);
            }
            issue.containArticle = articles;
            issue = parseJsonDataFromAsset();
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
        WeekFragment weekFragment = gson.fromJson(reader, WeekFragment.class);
        final Issue issue = getIssue(weekFragment);
        InchoateApplication.setNewestIssueCache(issue);
        mIssue = issue;
        final InchoateDBHelper helper = new InchoateDBHelper(getActivity(), null, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                helper.insertWholeData(issue);
            }
        }).start();
        mArticlesList = issue.containArticle;
        return issue;
    }

    public void weekFragmentTest() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.WEEK_FRAGMENT_QUERY_URL)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "weekFragmentTest: onFailure: e = " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonResult = response.body().string();
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
                WeekFragment weekFragment = gson.fromJson(jsonResult, WeekFragment.class);
                Issue issue = getIssue(weekFragment);
                InchoateApplication.setNewestIssueCache(issue);
                mArticlesList = issue.containArticle;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EconomistPlayerTimberStyle.unbindToService(getActivity(), economistServiceConnection);
    }
}
