/**
 * This class had been forked and it was changed for own tasks from repository "Wi-Fi в метро" (pw.thedrhax.mosmetro, Moscow Wi-Fi autologin)
 * Copyright © 2015 Dmitry Karikh <the.dr.hax@gmail.com>
 */
package ru.lionzxy.bmstuwifi.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

public class WifiUtils {
    public static final String UNKNOWN_SSID = "<unknown ssid>";

    private final SharedPreferences settings;
    private final ConnectivityManager cm;
    private final WifiManager wm;

    public WifiUtils(@NonNull Context context) {
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /*
     * Read-only methods
     */

    // Clear SSID from platform-specific symbols
    private static String clear(String text) {
        return (text != null && !text.isEmpty()) ? text.replace("\"", "") : UNKNOWN_SSID;
    }

    // Wi-Fi connectivity conditions
    public boolean isConnected(String SSID) {
        if (!wm.isWifiEnabled()) return false;
        if (!getSSID().equalsIgnoreCase(SSID)) return false;
        return true;
    }

    // Get WifiInfo from Intent or, if not available, from WifiManager
    public WifiInfo getWifiInfo(Intent intent) {
        if (intent != null && Build.VERSION.SDK_INT >= 14) {
            WifiInfo result = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (result != null) return result;
        }
        return wm.getConnectionInfo();
    }

    // Get SSID from Intent's EXTRA_WIFI_INFO (API > 14)
    public String getSSID(Intent intent) {
        return clear(getWifiInfo(intent).getSSID());
    }

    // Get SSID directly from WifiManager
    public String getSSID() {
        return getSSID(null);
    }

    // Get current IP from WifiManager
    public int getIP() {
        return wm.getConnectionInfo().getIpAddress();
    }

    // Get main Wi-Fi state
    public boolean isEnabled() {
        return wm.isWifiEnabled();
    }

    // Get Network by type
    @Nullable
    @RequiresApi(21)
    public Network getNetwork(int type) {
        for (Network network : cm.getAllNetworks()) {
            NetworkInfo info = cm.getNetworkInfo(network);
            if (info != null && info.getType() == type) {
                return network;
            }
        }
        return null;
    }

    // Get VPN (if active) or Wi-Fi Network object
    @Nullable
    @RequiresApi(21)
    public Network getNetwork() {
        Network result = getNetwork(ConnectivityManager.TYPE_VPN);
        if (result == null) {
            result = getNetwork(ConnectivityManager.TYPE_WIFI);
        }
        return result;
    }

    /*
     * Control methods
     */

    // Reconnect to SSID (only if already configured)
    public void reconnect(String SSID) {
        try {
            for (WifiConfiguration network : wm.getConfiguredNetworks()) {
                if (clear(network.SSID).equals(SSID)) {
                    wm.enableNetwork(network.networkId, true);
                    wm.reassociate();
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    // Bind to Network
    @RequiresApi(21)
    private void bindToNetwork(@Nullable Network network) {
        if (Build.VERSION.SDK_INT < 23) {
            try {
                ConnectivityManager.setProcessDefaultNetwork(network);
            } catch (IllegalStateException ignored) {
            }
        } else {
            cm.bindProcessToNetwork(network);
        }
    }

    // Bind current process to Wi-Fi
    // Refactored answer from Stack Overflow: http://stackoverflow.com/a/28664841
    public void bindToWifi() {
        if (!settings.getBoolean("pref_wifi_bind", true)) return;

        if (Build.VERSION.SDK_INT < 21)
            cm.setNetworkPreference(ConnectivityManager.TYPE_WIFI);
        else
            bindToNetwork(getNetwork());
    }

    // Report connectivity status to system
    @RequiresApi(21)
    public void report(boolean status) {
        Network network = getNetwork();
        if (network == null) return;

        if (Build.VERSION.SDK_INT >= 23)
            cm.reportNetworkConnectivity(network, status);
        else
            cm.reportBadNetwork(network);
    }
}
