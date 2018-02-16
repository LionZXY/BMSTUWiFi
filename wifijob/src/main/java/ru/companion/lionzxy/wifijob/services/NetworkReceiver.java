/**
 * This class had been forked and it was changed for own tasks from repository "Wi-Fi в метро" (pw.thedrhax.mosmetro, Moscow Wi-Fi autologin)
 * Copyright © 2015 Dmitry Karikh <the.dr.hax@gmail.com>
 */

package ru.companion.lionzxy.wifijob.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

import net.grandcentrix.tray.AppPreferences;

import java.util.Locale;

import ru.companion.lionzxy.wifijob.utils.Logger;
import ru.companion.lionzxy.wifijob.utils.WifiUtils;

/**
 * This BroadcastReceiver filters and sends Intents to the ConnectionService.
 * <p>
 * There are two types of Intents accepted by the ConnectionService:
 * 1) Wi-Fi network is definitely connected (startService())
 * 2) No Wi-Fi networks are connected (stopService())
 * <p>
 * NetworkReceiver doesn't take care of:
 * 1) Ignoring duplicated Intents
 * 2) Determining if current SSID is supported by the Provider
 *
 * @author Dmitry Karikh <the.dr.hax@gmail.com>
 * @see ConnectionService
 */
public class NetworkReceiver extends BroadcastReceiver {
    private Context context;
    private Intent intent;

    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        // Stop if Intent is empty
        if (intent == null || intent.getAction() == null)
            return;

        // Stop if automatic connection is disabled in settings
        AppPreferences settings = new AppPreferences(context);
        if (!settings.getBoolean("pref_autoconnect", true))
            return;

        // If Wi-Fi is disabled, stop ConnectionService immediately
        WifiUtils wifi = new WifiUtils(context);
        if (!wifi.isEnabled()) {
            Logger.log(this, "Wi-Fi not enabled");
            stopService();
            return;
        }

        switch (intent.getAction()) {
            /*
              Listen to all Wi-Fi state changes and start ConnectionService if Wi-Fi is connected
              Also check SupplicantState for better results
             */
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                SupplicantState state = wifi.getWifiInfo(intent).getSupplicantState();
                if (state == null) break;

                Logger.log(this, String.format(Locale.ENGLISH, "Intent: %s (%s)",
                        intent.getAction(), state.name()
                ));

                switch (state) {
                    case COMPLETED:
                    case ASSOCIATED: // This appears randomly between multiple CONNECTED states
                        startService();
                        break;
                    case SCANNING: // Some devices do not report DISCONNECTED state so...
                    case DISCONNECTED:
                        stopService();
                        break;
                }

                break;

            default:
                Logger.log(this, "Unknown Intent: " + intent.getAction());
        }
    }

    /**
     * Start ConnectionService and pass received Intent's content
     */
    private void startService() {
        Intent service = new Intent(context, ConnectionService.class);
        service.setAction(intent.getAction());
        service.putExtras(intent);
        context.startService(service);
    }

    /**
     * Stop ConnectionService
     */
    private void stopService() {
        context.startService(new Intent(context, ConnectionService.class).setAction("STOP"));
    }
}
