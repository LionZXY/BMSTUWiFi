package ru.lionzxy.bmstuwifi.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
    private Logger logger = Logger.getLogger();
    private WifiManager wifiManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.getLogger().init(getBaseContext());
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        connectionThread = new DetectStateThread(getBaseContext(), new BMSTUStudentAuth(logger));
        logger.log(TAG, Logger.Level.DEBUG, "Service created");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && ACTION_STOP.equals(intent.getAction()) || !wifiManager.isWifiEnabled()) {
            connectionThread.interrupt();
            logger.log(TAG, Logger.Level.DEBUG, "Stop service");
            stopSelf();
            return START_NOT_STICKY;
        }

        if (connectionThread.getState().equals(Thread.State.TERMINATED))
            connectionThread = new DetectStateThread(this, new BMSTUStudentAuth(logger));

        if (connectionThread.getState().equals(Thread.State.NEW))
            connectionThread.start();

        logger.log(TAG, Logger.Level.DEBUG, "On start command");

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
        logger.log(TAG, Logger.Level.DEBUG, "Destroy service");
        connectionThread.interrupt();
    }
}
