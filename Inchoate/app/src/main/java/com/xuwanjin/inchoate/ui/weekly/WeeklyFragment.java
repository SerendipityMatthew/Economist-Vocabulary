package com.xuwanjin.inchoate.ui.weekly;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
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
import com.xuwanjin.inchoate.events.SlidingUpControllerEvent;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.ArticleCategorySection;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.model.week.WeekFragment;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;

import static com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle.mEconomistService;

import com.xuwanjin.inchoate.timber_style.IEconomistService;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<Article> mArticlesList;
    FloatingActionButton mFab;
    private HashMap<String, List<Article>> issueHashMap = new HashMap<>();
    View mDownloadAudio;
    View mStreamAudio;
    TextView issueDate;
    TextView magazineHeadline;
    ImageView magazineCover;
    WeeklyAdapter mWeeklyAdapter;
    StickHeaderDecoration mStickHeaderDecoration;
    View view;
    private Issue mIssue;
    public static final int FETCH_DATA_AND_NOTIFY_MSG = 1000;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == FETCH_DATA_AND_NOTIFY_MSG) {
                if (mWeeklyAdapter != null) {
                    mWeeklyAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekly, container, false);
        initView();
        ServiceConnection serviceConnection = new ServiceConnection() {
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
        EconomistPlayerTimberStyle.binToService(getActivity(), serviceConnection);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        issueContentRecyclerView.setLayoutManager(linearLayoutManager);
        int sectionToPosition = InchoateApplication.getScrollToPosition();
        if (sectionToPosition > 0) {
            int scrollToPosition = Utils.getArticleSumBySection(sectionToPosition - 2);
            linearLayoutManager.scrollToPosition(scrollToPosition);
        }

        mArticlesList = initData(new ArrayList<Article>());
        mWeeklyAdapter = new WeeklyAdapter(mArticlesList, getContext(), this);
        issueContentRecyclerView.setAdapter(mWeeklyAdapter);
        mWeeklyAdapter.setHeaderView(mSectionHeaderView);
        mWeeklyAdapter.setFooterView(mFooterView);

        mStickHeaderDecoration = new StickHeaderDecoration(issueContentRecyclerView, getContext());
        issueContentRecyclerView.addItemDecoration(mStickHeaderDecoration);
        mFab.setFocusable(true);
        mFab.setClickable(true);
        mFab.setVisibility(View.VISIBLE);

        initOnClickListener();
        return view;
    }

    public void initView() {
        issueContentRecyclerView = view.findViewById(R.id.issue_content_recyclerView);

        // 这种 header 的出现, 他会 inflate 在 RecyclerView 的上面, 这个时候, 画第一个 item 的 header,
        //也会出现在 RecyclerView 的上面, 但是他会出现, HeaderView 的上面
        mSectionHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.weekly_section_header, issueContentRecyclerView, false);
        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.weekly_footer, issueContentRecyclerView, false);

        mFab = view.findViewById(R.id.issue_category_fab);
        previousEdition = mSectionHeaderView.findViewById(R.id.previous_edition);
        mDownloadAudio = mSectionHeaderView.findViewById(R.id.download_audio);
        mStreamAudio = mSectionHeaderView.findViewById(R.id.stream_audio);
        issueDate = mSectionHeaderView.findViewById(R.id.issue_date);
        magazineCover = mSectionHeaderView.findViewById(R.id.magazine_cover);
        magazineHeadline = mSectionHeaderView.findViewById(R.id.magazine_headline);
    }

    public void initOnClickListener() {
        mStreamAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        InchoateApplication.setDisplayArticleCache(article);
                        InchoateApplication.setAudioPlayingArticleListCache(InchoateApplication.getNewestIssueCache().get(0).containArticle);
                        SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                        panelState.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                        EventBus.getDefault().post(panelState);
                        if (mIssue != null) {
                            EconomistPlayerTimberStyle.playWholeIssue(mIssue.containArticle.get(0), mIssue);
                        }
                    }
                }).start();

            }
        });
        final Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                ArrayList<Article> audioArticle = new ArrayList<>();
                for (Article article : mIssue.containArticle) {
                    if (article.audioUrl != null
                            && !article.audioUrl.trim().equals("")) {
                        audioArticle.add(article);
                    }
                }

                File commonFile = getActivity().getExternalCacheDirs()[0];

                //     issueDate/Section/article_title
                //N 个     article_audio_url
                //
                final String issueDate = mIssue.issueDate;
                for (final Article article : mIssue.containArticle) {
                    String section = article.section;
                    String audioFile = commonFile.getAbsolutePath() + "/" + issueDate + "/" + section;
                    String noSpacePath = audioFile.replace(" ", "_");
                    String noSpaceName = article.title.replace(" ", "_");
                    DownloadTask task =
                            new DownloadTask
                                    .Builder(article.audioUrl, noSpacePath, noSpaceName + ".mp3")
                                    .build();
                    final DownloadListener downloadListener = new DownloadListener() {
                        @Override
                        public void taskStart(@NonNull DownloadTask task) {
                            Log.d(TAG, "taskStart: task.getFilename = " + task.getFilename());

                        }

                        @Override
                        public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {

                        }

                        @Override
                        public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

                        }

                        @Override
                        public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {

                        }

                        @Override
                        public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {

                        }

                        @Override
                        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

                        }

                        @Override
                        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

                        }

                        @Override
                        public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {

                        }

                        @Override
                        public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {

                        }

                        @Override
                        public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {

                        }

                        @Override
                        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                            String localeAudioUrl = task.getParentFile().getAbsolutePath()+ "/" + task.getFilename();
                            InchoateDBHelper helper = new InchoateDBHelper(getContext(),null, null);
                            article.localeAudioUrl = localeAudioUrl;
                            Log.d(TAG, "taskEnd: localeAudioUrl = " + localeAudioUrl);
                            helper.updateArticleAudioLocaleUrl(article, issueDate);
                        }
                    };
                    task.execute(downloadListener);
                }
            }
        };

        mDownloadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(mRunnable).start();
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

    private List<Article> initData(List<Article> articles) {
        List<Issue> issueList = InchoateApplication.getNewestIssueCache();
        final Issue issue;
        if (issueList != null && issueList.size() > 0) {
            InchoateDBHelper helper = new InchoateDBHelper(getContext(), null, null);
            String issueDateStr = "May 9th 2020";
            issue = helper.queryIssueByIssueDate(issueDateStr).get(0);
            mIssue = helper.queryIssueByIssueDate(issueDateStr).get(0);
            articles = issue.containArticle;
            Log.d(TAG, "onResponse: use the cache ");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(mSectionHeaderView)
                            .load(issue.coverImageUrl)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.d(TAG, "onLoadFailed: ");
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Log.d(TAG, "onResourceReady: ");
                                    return false;
                                }
                            })
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.mipmap.magazine_cover)
                            .into(magazineCover);
                    issueDate.setText(issue.issueDate);
                    magazineHeadline.setText(issue.headline);
                }
            });
        } else {
            for (int i = 0; i < 82; i++) {
                Article article = new Article();
                article.summary = "heeeeeeeeee" + i;
                article.section = "Matthew, helllo";
                article.headline = "Matthew = " + ArticleCategorySection.BRIEFING;
                articles.add(article);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    parseJsonDataFromAsset();
                }
            });
        }
        return articles;
    }

    public void parseJsonDataFromAsset() {
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
        Glide.with(mSectionHeaderView)
                .load(issue.coverImageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: ");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "onResourceReady: ");
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.magazine_cover)
                .into(magazineCover);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                issueDate.setText(issue.issueDate);
                magazineHeadline.setText(issue.headline);

            }
        });
        InchoateApplication.setNewestIssueCache(issue);
        final InchoateDBHelper helper = new InchoateDBHelper(getActivity(), null, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                helper.insertWholeData(issue);
            }
        }).start();
        mArticlesList = issue.containArticle;
        mHandler.sendEmptyMessage(FETCH_DATA_AND_NOTIFY_MSG);

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
                mHandler.sendEmptyMessage(FETCH_DATA_AND_NOTIFY_MSG);
            }
        });
    }

}
