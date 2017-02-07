package ru.lionzxy.bmstuwifi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.net.HttpURLConnection;
import java.net.URL;

import ru.lionzxy.bmstuwifi.App;

/**
 * Created by lionzxy on 14.11.16.
 */

public class WiFiHelper {
    private static final String no_ssid = "0x";
    private static final String unknown_ssid = "<unknown_ssid>";

    public static boolean isUnknownSSID(String SSID) {
        return unknown_ssid.equals(SSID) || no_ssid.equals(SSID);
    }


    public static boolean isConnected(Context context, boolean strict) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null)
            return false;
        if (strict) {
            HttpURLConnection urlConnection = null;
            try {
                switchToWifiNetwork(cm);
                urlConnection = (HttpURLConnection) new URL("http://google.ru/generate_204").openConnection();
                return urlConnection.getResponseCode() == 204;
            } catch (Exception ex) {
                return false;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        } else {
            return activeNetwork.isConnectedOrConnecting();
        }
    }

    public static boolean isConnected() {
        return isConnected(App.get(), App.get().getSharedPreferences().getBoolean("pref_wifi_check_strict", true));
    }

    //Try set default wifi connection
    public static void switchToWifiNetwork(ConnectivityManager cm) {
        try {
            if (Build.VERSION.SDK_INT >= 23)
                cm.bindProcessToNetwork(getWifiNetwork(cm));
            else if (Build.VERSION.SDK_INT >= 21)
                ConnectivityManager.setProcessDefaultNetwork(getWifiNetwork(cm));
        } catch (Exception ignored) {
        }
    }

    public static Network getWifiNetwork(ConnectivityManager manager) {
        if (Build.VERSION.SDK_INT >= 21) {
            Network[] networks = manager.getAllNetworks();
            NetworkInfo networkInfo;
            Network network;
            for (Network network1 : networks) {
                network = network1;
                networkInfo = manager.getNetworkInfo(network);
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
                    return network;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return manager.getActiveNetwork();
            }
        }
        return null;
    }

    public static String getCurrentSSID(Context context){
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().replaceAll("\"","");
    }
}
