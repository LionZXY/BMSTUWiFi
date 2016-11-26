package ru.lionzxy.bmstuwifi.events.handlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import ru.lionzxy.bmstuwifi.services.ConnectionService;

/**
 * Created by lionzxy on 04.11.16.
 */

public class ChangeWifiStateHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, ConnectionService.class);
        service.setAction(intent.getAction());
        service.putExtras(intent);

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_autoconnect", true))
            context.startService(service);
    }
}
