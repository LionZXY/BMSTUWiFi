package ru.lionzxy.bmstuwifi.tasks;

import android.net.wifi.WifiManager;

import ru.lionzxy.bmstuwifi.R;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITaskStateResponse;

/**
 * Created by lionzxy on 12.11.16.
 */

public class WaitForIpTask extends ITask {
    private String TAG = "WaitForIp";
    private WifiManager manager;
    private int pref_ip_wait;

    public WaitForIpTask(WifiManager wifiManager, int pref_ip_wait) {
        this.manager = wifiManager;
        this.pref_ip_wait = pref_ip_wait;

    }

    @Override
    public boolean runTask() {
        int count = 0;

        while (manager.getConnectionInfo().getIpAddress() == 0 && !isInterrupt()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            if (pref_ip_wait != 0) {
                onStateChange(R.string.wait_ip, count, pref_ip_wait);

                if (count++ == pref_ip_wait) { // Timeout condition
                    onStateChange(R.string.wait_ip_error, ITaskStateResponse.ERROR_STATE, pref_ip_wait);
                    return false;
                }
            } else onStateChange(R.string.wait_ip, count, ITaskStateResponse.INFINITE_STATES);
        }

        return true;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
