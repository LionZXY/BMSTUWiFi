package ru.lionzxy.bmstuwifi.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import ru.lionzxy.bmstuwifi.R;
import ru.lionzxy.bmstuwifi.authentificator.IAuth;
import ru.lionzxy.bmstuwifi.tasks.AuthTask;
import ru.lionzxy.bmstuwifi.tasks.WaitForIpTask;
import ru.lionzxy.bmstuwifi.tasks.WaitSSID;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITaskStateResponse;
import ru.lionzxy.bmstuwifi.tasks.interfaces.TaskResponseWithNotification;
import ru.lionzxy.bmstuwifi.utils.Logger;
import ru.lionzxy.bmstuwifi.utils.Notification;

import static ru.lionzxy.bmstuwifi.utils.WiFiHelper.isConnected;

/**
 * Created by lionzxy on 12.11.16.
 */
    
public class DetectStateThread extends Thread {
    private static final String TAG = "DetectState";

    private Context context;
    private WifiManager wifiManager;
    private SharedPreferences settings;
    private Notification notification;
    private IAuth auth;
    private ITask currentTask = null;

    public DetectStateThread(Context context, IAuth auth) {
        super();
        this.context = context;
        this.auth = auth;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.notification = new Notification(context)
                .setEnabled(true)
                .setId(1)
                .setTitle(context.getResources().getString(R.string.notfication_title))
                .setIcon(R.drawable.ic_stat_logo);
    }

    @Override
    public void run() {
        //State 1 - WaitSSID and check
        WaitSSID waitSSID = (WaitSSID) new WaitSSID(wifiManager, Integer.parseInt(settings.getString("pref_ssid_wait", "10")))
                .subscribeOnStateChange(new ITaskStateResponse() {
                    @Override
                    public void onStateChange(String TAG, int stateDescribtionResId, int stateNumber, int stateCount) {
                        Logger.getLogger().log(TAG, Logger.Level.DEBUG, stateDescribtionResId);
                    }
                });
        currentTask = waitSSID;
        if (waitSSID.runTask()) {
            if (!auth.isValidSSID(waitSSID.Last_SSID))
                return;
        } else {
            onError();
            return;
        }

        if (isInterrupted())
            return;


        //State 2 - Wait For IP
        WaitForIpTask waitForIpTask = (WaitForIpTask) new WaitForIpTask(wifiManager, Integer.parseInt(settings.getString("pref_ip_wait", "30")))
                .subscribeOnStateChange(new TaskResponseWithNotification(notification));
        currentTask = waitForIpTask;
        if (waitForIpTask.runTask()) {
            notification.setContinuous().setText(R.string.wait_ip_sucs).show();
            Logger.getLogger().log(TAG, Logger.Level.DEBUG, "Ip Waiting sucs");
        } else {
            onError();
            return;
        }

        if (isInterrupted())
            return;

        if (isConnected(context, settings.getBoolean("pref_wifi_check_strict", true))) {
            onFinished();
            return;
        }

        if (isInterrupted())
            return;

        //State 3 - Auth
        AuthTask authTask = (AuthTask) new AuthTask(context).subscribeOnStateChange(new TaskResponseWithNotification(notification));
        currentTask = authTask;
        if (authTask.runTask()) {
            onFinished();
            Logger.getLogger().log(TAG, Logger.Level.INFO, "YAY!");
        } else Logger.getLogger().log(TAG, Logger.Level.INFO, ":(");

    }

    void onError() {
        Logger.getLogger().log(TAG, Logger.Level.INFO, context.getString(R.string.auth_err));
        notification.setText(R.string.auth_err).setProgress(0, 0).show();
    }

    void onFinished() {
        notification.setText(R.string.auth_finished).setProgress(0, 0).show();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (currentTask != null)
            currentTask.interrupt();
        if (notification != null)
            notification.hide();
        Logger.getLogger().log(TAG, Logger.Level.INFO, "Поток превран");
    }
}
