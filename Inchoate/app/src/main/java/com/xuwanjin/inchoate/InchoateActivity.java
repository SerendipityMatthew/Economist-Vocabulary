package com.xuwanjin.inchoate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.xuwanjin.inchoate.events.SlidingUpControllerEvent;
import com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle;
import com.xuwanjin.inchoate.timber_style.IEconomistService;
import com.xuwanjin.inchoate.ui.playing.AudioPlayerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.xuwanjin.inchoate.timber_style.EconomistPlayerTimberStyle.setEconomistService;


public class InchoateActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationController {
    public static final String TAG = "InchoateActivity";
    private NavController mController;
    private BottomNavigationView mBottomNavigationView;
    private FrameLayout mNowPlayingControl;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private IEconomistService mEconomistService;
    private boolean isSuccess = false;
    private AudioPlayerFragment mAudioPlayerFragment = new AudioPlayerFragment();
    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private PanelState mPanelState = null;
    ServiceConnection economistServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mEconomistService = IEconomistService.Stub.asInterface(service);
            setEconomistService(mEconomistService);
            isSuccess = true;
            Log.d(TAG, "onServiceConnected: mEconomistService = " + mEconomistService);
            if (isAudioPlying()) {
                collapsedTheAudioPlayerFragment(PanelState.COLLAPSED);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            mEconomistService = null;
            setEconomistService(null);
            isSuccess = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();

    }

    private void initView() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mSlidingUpPanelLayout = findViewById(R.id.slide_layout);
        mNowPlayingControl = findViewById(R.id.now_playing_control);

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mController = Navigation.findNavController(this, R.id.nav_host_fragment);
        InchoateApp.NAVIGATION_CONTROLLER = mController;
        // 第一次启动的时候隐藏 SlidingUpLayout ,
        mSlidingUpPanelLayout.setPanelHeight(30);
        mSlidingUpPanelLayout.setTouchEnabled(true);
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (mAudioPlayerFragment.getAudioPlayingBar() != null) {
                    mAudioPlayerFragment.getAudioPlayingBar().setVisibility(slideOffset > 0.3 ? View.GONE : View.VISIBLE);
                }
                if (slideOffset > 0.3) {

                }
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                mPanelState = newState;
                if (newState == PanelState.EXPANDED) {
                    panel.setFocusableInTouchMode(true);
                    panel.requestFocus();
                    panel.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_UP
                                    && keyCode == KeyEvent.KEYCODE_BACK) {
                                collapsedTheAudioPlayerFragment(PanelState.COLLAPSED);
                                return true;
                            }
                            return false;
                        }
                    });
                } else {
                    panel.setOnKeyListener(null);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean isAudioPlying() {
        return EconomistPlayerTimberStyle.isPlaying();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSlidingPanel(SlidingUpControllerEvent event) {
        inflateAudioPlaying(event.panelState);
    }

    private void inflateAudioPlaying(PanelState panelState) {
        mFragmentManager
                .beginTransaction()
                .replace(R.id.now_playing_control, mAudioPlayerFragment)
                .commitAllowingStateLoss();
        mSlidingUpPanelLayout.setPanelState(panelState);
        mSlidingUpPanelLayout.setPanelHeight(400);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (mPanelState == PanelState.EXPANDED) {
            collapsedTheAudioPlayerFragment(PanelState.COLLAPSED);
        }
        if (item.getItemId() == R.id.item_today) {
            Utils.navigationController(mController, R.id.navigation_today);
        } else if (item.getItemId() == R.id.item_weekly) {
            Utils.navigationController(mController, R.id.navigation_weekly);
        } else if (item.getItemId() == R.id.item_bookmark) {
            Utils.navigationController(mController, R.id.navigation_bookmark);
        } else if (item.getItemId() == R.id.item_setting) {
            Utils.navigationController(mController, R.id.navigation_settings);
        }

        return true;
    }

    private void collapsedTheAudioPlayerFragment(PanelState panelState) {
        SlidingUpControllerEvent controllerEvent = new SlidingUpControllerEvent();
        controllerEvent.panelState = panelState;
//        EventBus.getDefault().post(panelState);
        inflateAudioPlaying(panelState);
    }


    @Override
    public void isShowBottomNavigation(boolean isShow) {
        if (isShow) {
            mBottomNavigationView = findViewById(R.id.bottom_navigation);
            mBottomNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        } else {
            mBottomNavigationView.removeAllViews();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mController = null;
    }
}
