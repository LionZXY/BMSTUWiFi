package ru.lionzxy.bmstuwifi.events.handlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import ru.lionzxy.bmstuwifi.services.ConnectionService;
import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 04.11.16.
 */

public class ChangeWifiStateHandler extends BroadcastReceiver {
    public static final String TAG = "ChangeWifi";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, ConnectionService.class);
        service.setAction(intent.getAction());
        service.putExtras(intent);

        Logger.getLogger().log(TAG, Logger.Level.DEBUG, "Поймано событие измененения WiFi");

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_autoconnect", true))
            context.startService(service);
    }
}
