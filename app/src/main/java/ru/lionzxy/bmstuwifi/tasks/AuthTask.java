package ru.lionzxy.bmstuwifi.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.lionzxy.bmstuwifi.App;
import ru.lionzxy.bmstuwifi.LoginActivity_;
import ru.lionzxy.bmstuwifi.R;
import ru.lionzxy.bmstuwifi.authentificator.IAuth;
import ru.lionzxy.bmstuwifi.interfaces.ICanOpenActivity;
import ru.lionzxy.bmstuwifi.interfaces.ITask;
import ru.lionzxy.bmstuwifi.interfaces.ITaskStateResponse;
import ru.lionzxy.bmstuwifi.utils.Constant;
import ru.lionzxy.bmstuwifi.utils.WiFiHelper;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

import static ru.lionzxy.bmstuwifi.utils.WiFiHelper.switchToWifiNetwork;

/**
 * Created by lionzxy on 12.11.16.
 */

public class AuthTask extends ITask {
    public static final String TAG = "AuthTask";
    private int pref_auth_login_count;
    private final OkHttpClient client;
    private IAuth auth;

    public AuthTask(IAuth auth) {
        pref_auth_login_count = PreferenceManager.getDefaultSharedPreferences(App.get()).getInt("pref_auth_login_count", 3);
        this.auth = auth;
        this.client = new OkHttpClient();
    }

    @Override
    public boolean runTask() {
        int count = 0;
        String login = this.auth.getLogin(null);
        String password = this.auth.getPassword(null);

        if (login == null || password == null) {
            onStateChange(R.string.auth_err_login);
            for (WeakReference<ITaskStateResponse> taskStateResponseWeakReference : this.taskWeakResponses)
                if (taskStateResponseWeakReference.get() != null && taskStateResponseWeakReference.get() instanceof ICanOpenActivity) {
                    Bundle extra = new Bundle();
                    extra.putString("wifi_ssid", auth.getSSID());
                    ((ICanOpenActivity) taskStateResponseWeakReference.get()).openActivity(LoginActivity_.class, null, null);
                }
            return false;
        }
        onStateChange(R.string.auth);
        while (!isConnected() && !isInterrupt()) {
            onStateChange(R.string.auth, count, pref_auth_login_count);
            switchToWifiNetwork((ConnectivityManager) App.get().getSystemService(Context.CONNECTIVITY_SERVICE));

            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("redirurl", "/")
                        .add("auth_user", login)
                        .add("auth_pass", password)
                        .add("accept", "Continue")
                        .build();
                Request request = new Request.Builder()
                        .url(auth.getAuthSite())
                        .post(formBody)
                        .build();

                onStateChange(R.string.auth_send_data, count, pref_auth_login_count);
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(App.get());
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Logger.getLogger().log(TAG, Logger.Level.DEBUG, "logout_id: " + settings.getString("logout_id", "null"));
                    onStateChange(R.string.auth_finished);
                    String resp = response.body().string();
                    Logger.getLogger().log(TAG, Logger.Level.DEBUG, resp);
                    Matcher matcher = Pattern.compile(Constant.REG_EXP).matcher(resp);
                    if (matcher.find()) {
                        String logout_id = matcher.group(1);
                        logout_id = logout_id.substring(1, logout_id.length() - 2);
                        settings.edit().putString("logout_id", logout_id).apply();
                        Logger.getLogger().log(TAG, Logger.Level.INFO, "Авторизация прошла успешно. logout_id = " + logout_id);
                    } else
                        Logger.getLogger().log(TAG, Logger.Level.INFO, "Не удалось найти logout_id!");
                    Logger.getLogger().log(TAG, Logger.Level.DEBUG, "logout_id: " + settings.getString("logout_id", "null"));
                } else onStateChange(R.string.auth_err, count, pref_auth_login_count);


            } catch (IOException e) {
                e.printStackTrace();
                onStateChange(R.string.auth_err, count, pref_auth_login_count);
            }

            if (count++ == pref_auth_login_count) {
                onStateChange(R.string.auth_err);
                return false;
            }
        }
        onStateChange(R.string.auth_finished);
        return true;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    private boolean isConnected() {
        return WiFiHelper.isConnected(App.get(), PreferenceManager.getDefaultSharedPreferences(App.get()).getBoolean("pref_wifi_check_strict", true));
    }


}
