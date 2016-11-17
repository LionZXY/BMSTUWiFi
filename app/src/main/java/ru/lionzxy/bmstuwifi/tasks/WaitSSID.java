package ru.lionzxy.bmstuwifi.tasks;

import android.net.wifi.WifiManager;
import android.util.Log;

import ru.lionzxy.bmstuwifi.R;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITaskStateResponse;
import ru.lionzxy.bmstuwifi.utils.WiFiHelper;

/**
 * Created by lionzxy on 12.11.16.
 */

public class WaitSSID extends ITask {
    private WifiManager manager;
    private int pref_ssid_wait;
    public String Last_SSID = null;

    public WaitSSID(WifiManager wifiManager, int pref_ssid_wait) {
        this.manager = wifiManager;
        this.pref_ssid_wait = pref_ssid_wait * 10;

    }

    @Override
    public boolean runTask() {
        int count = 0;

        while (WiFiHelper.isUnknownSSID((Last_SSID = manager.getConnectionInfo().getSSID())) && !isInterrupt()) {
            Log.i("WaitSSID", Last_SSID);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }

            if (pref_ssid_wait != 0) {
                onStateChange(R.string.wait_ssid, count, pref_ssid_wait);

                if (count++ == pref_ssid_wait) { // Timeout condition
                    onStateChange(R.string.wait_ip_error, ITaskStateResponse.ERROR_STATE, pref_ssid_wait);
                    return false;
                }
            } else onStateChange(R.string.wait_ssid, count, ITaskStateResponse.INFINITE_STATES);
        }
        return true;
    }

}
