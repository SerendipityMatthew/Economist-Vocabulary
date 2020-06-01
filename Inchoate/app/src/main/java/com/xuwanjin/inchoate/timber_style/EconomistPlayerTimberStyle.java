package com.xuwanjin.inchoate.timber_style;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.xuwanjin.inchoate.Constants;
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;


public class EconomistPlayerTimberStyle {
    public static IEconomistService mEconomistService;

    public static final boolean binToService(Context context, ServiceConnection serviceConnection) {
        context.startService(new Intent(context, EconomistServiceTimberStyle.class));
        ServiceBinder serviceBinder = new ServiceBinder(serviceConnection, context);
        boolean success = context.bindService(new Intent().setClass(context, EconomistServiceTimberStyle.class), serviceBinder, 0);
        return success;
    }

    public static final void unbindToService(Context context, ServiceConnection serviceConnection) {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
    }

    public static void play(String audioPath) {
        try {
            mEconomistService.openFile(audioPath);

            mEconomistService.play();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void seekToPosition(int position) {
        try {
            mEconomistService.seekToPosition(position);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPlaying() {

        try {
            return mEconomistService.isPlaying();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getCurrentPosition() {
        try {
            return mEconomistService.getCurrentPosition();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getDuration() {
        try {
            return mEconomistService.getDuration();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void playWholeIssue(Article currentArticle, Issue currentIssue, int sourceFlag) {
        try {
            mEconomistService.playTheRestByIssueDate(currentArticle, currentIssue.issueDate, sourceFlag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void playWholeIssueByIssueDate(Article currentArticle, String issueDate) {
        try {
            mEconomistService.playTheRestByIssueDate(currentArticle, issueDate, Constants.WEEKLY_PLAYING_SOURCE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void seekToIncrementPosition() {
        try {
            mEconomistService.seekToIncrementPosition();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void playOrPause() {
        try {
            mEconomistService.playOrPause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void playNext() {
        try {
            mEconomistService.next();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void playPrevious() {
        try {
            mEconomistService.previous();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static final class ServiceToken {
        public ContextWrapper mContextWrapper;

        public ServiceToken(ContextWrapper contextWrapper) {
            this.mContextWrapper = contextWrapper;
        }
    }

    public static final class ServiceBinder implements ServiceConnection {
        private final ServiceConnection mServiceConnection;
        private final Context mContext;

        public ServiceBinder(ServiceConnection serviceConnection, Context context) {
            mServiceConnection = serviceConnection;
            mContext = context;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mEconomistService = IEconomistService.Stub.asInterface(service);
            if (mServiceConnection != null) {
                mServiceConnection.onServiceConnected(name, service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mServiceConnection != null) {
                mServiceConnection.onServiceDisconnected(name);
            }
            mEconomistService = null;
        }
    }
}
