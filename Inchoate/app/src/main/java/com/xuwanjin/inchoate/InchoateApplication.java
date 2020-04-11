package com.xuwanjin.inchoate;

import android.app.Application;
import android.os.StrictMode;

public class InchoateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy
                .Builder()
                .detectNetwork()
                .detectDiskReads()
                .detectDiskWrites()
                .penaltyDialog()
                .detectDiskReads()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .setClassInstanceLimit(InchoateActivity.class,1)
                .build());
    }
}
