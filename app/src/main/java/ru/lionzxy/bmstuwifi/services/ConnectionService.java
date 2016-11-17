package ru.lionzxy.bmstuwifi.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import ru.lionzxy.bmstuwifi.authentificator.BMSTUStudentAuth;
import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 04.11.16.
 */

public class ConnectionService extends Service {
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_SHORTCUT = "SHORTCUT";
    public static final String TAG = "ConnectionService";

    private Thread connectionThread;
    private Logger logger = new Logger();
    private WifiManager wifiManager;
    
    @Override
    public void onCreate() {
        super.onCreate();

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        connectionThread = new DetectStateThread(getBaseContext(), new BMSTUStudentAuth(logger, this));
        Log.i(TAG, "On Create");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && ACTION_STOP.equals(intent.getAction()) || !wifiManager.isWifiEnabled()) {
            connectionThread.interrupt();
            stopSelf();
            return START_NOT_STICKY;
        }

        if (connectionThread.getState().equals(Thread.State.TERMINATED))
            connectionThread = new DetectStateThread(this, new BMSTUStudentAuth(logger, this));

        if (connectionThread.getState().equals(Thread.State.NEW))
            connectionThread.start();

        Log.i(TAG, "On Start Command");

        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectionThread.interrupt();
    }
}
