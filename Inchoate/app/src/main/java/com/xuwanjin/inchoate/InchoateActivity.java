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
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
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
    NavController controller;
    BottomNavigationView bottomNavigationView;
    ConstraintLayout mConstraintLayout;
    FrameLayout nowPlayingControl;
    public SlidingUpPanelLayout slidingUpPanelLayout;
    public IEconomistService mEconomistService;
    private boolean isSuccess = false;
    ServiceConnection economistServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mEconomistService = IEconomistService.Stub.asInterface(service);
            setEconomistService(mEconomistService);
            isSuccess = true;
            Log.d(TAG, "onServiceConnected: mEconomistService = " + mEconomistService);
            if (isAudioPlying()){
                SlidingUpControllerEvent panelState = new SlidingUpControllerEvent();
                panelState.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                EventBus.getDefault().post(panelState);
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
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        InchoateApp.NAVIGATION_CONTROLLER = controller;
        // 第一次启动的时候隐藏 SlidingUpLayout ,
        slidingUpPanelLayout.setPanelHeight(30);
    }

    public void initView() {
        mConstraintLayout = findViewById(R.id.main_activity);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        slidingUpPanelLayout = findViewById(R.id.slide_layout);
        nowPlayingControl = findViewById(R.id.now_playing_control);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EconomistPlayerTimberStyle.binToService(this, economistServiceConnection);
    }
    public boolean isAudioPlying(){
       return EconomistPlayerTimberStyle.isPlaying();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSlidingPanel(SlidingUpControllerEvent event) {
        inflateAudioPlaying(event.panelState);
    }

    public void inflateAudioPlaying(SlidingUpPanelLayout.PanelState panelState) {
        Log.d("Matthew", "inflateAudioPlaying: ");
        final AudioPlayerFragment audioPlayerFragment = new AudioPlayerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.now_playing_control, audioPlayerFragment)
                .commitAllowingStateLoss();
        slidingUpPanelLayout.setPanelState(panelState);
        slidingUpPanelLayout.setTouchEnabled(true);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (audioPlayerFragment.getAudioPlayingBar() != null){
                     audioPlayerFragment.getAudioPlayingBar().setVisibility(slideOffset > 0.3 ? View.GONE : View.VISIBLE);
                }
                if (slideOffset > 0.3){

                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
        slidingUpPanelLayout.setPanelHeight(400);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_today) {
            Utils.navigationController(controller, R.id.navigation_today);
        } else if (item.getItemId() == R.id.item_weekly) {
            Utils.navigationController(controller, R.id.navigation_weekly);
        } else if (item.getItemId() == R.id.item_bookmark) {
            Utils.navigationController(controller, R.id.navigation_bookmark);
        } else if (item.getItemId() == R.id.item_setting) {
            Utils.navigationController(controller, R.id.navigation_settings);
        }

        return false;
    }

    @Override
    public void isShowBottomNavigation(boolean isShow) {
        Log.d("Matthew", "isShowBottomNavigation: isShow = " + isShow);
        if (isShow) {
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.setOnNavigationItemSelectedListener(this);

        } else {
            bottomNavigationView.removeAllViews();
        }
    }
}
