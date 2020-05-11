package com.xuwanjin.inchoate.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.xuwanjin.inchoate.events.AudioPlayerEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlaybackController {
    public static final String TAG = "PlaybackController";
    private EconomistService mEconomistService;
    private Context mContext;
    public static boolean isServiceRunning = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof EconomistService.LocalBinder) {
                mEconomistService = ((EconomistService.LocalBinder) service).getService();
                Log.d(TAG, "onServiceConnected: ");
                isServiceRunning = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mEconomistService = null;
            isServiceRunning = false;
        }
    };
    public PlaybackController(Context context){
        this.mContext = context;
        bindToService();
    }
    public void init(){
        EventBus.getDefault().register(PlaybackController.this);
    }
    public void bindToService(){
        Intent intent = new Intent();
        intent.setClassName(mContext, EconomistService.class.getName());
        EventBus.getDefault().register(PlaybackController.this);
        Log.d(TAG, "bindToService: ");
        int flag = 0;
        Log.d(TAG, "bindToService: isServiceRunning = " + isServiceRunning);
        if (!isServiceRunning){
            flag = Context.BIND_AUTO_CREATE;
        }else {
            Log.d(TAG, "bindToService: the service had bound.");
        }
        mContext.bindService(intent, mServiceConnection, flag);
    }
    @Subscribe(threadMode = ThreadMode.MAIN , sticky = true)
    public void play(AudioPlayerEvent playerEvent){
        mEconomistService.play(playerEvent);
    }
}
