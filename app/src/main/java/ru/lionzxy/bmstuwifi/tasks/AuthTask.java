package ru.lionzxy.bmstuwifi.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
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
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;
import ru.lionzxy.bmstuwifi.utils.ConnectionHelper;
import ru.lionzxy.bmstuwifi.utils.WiFiHelper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static ru.lionzxy.bmstuwifi.utils.WiFiHelper.switchToWifiNetwork;

/**
 * Created by lionzxy on 12.11.16.
 */

public class AuthTask extends ITask {
    public static final String TAG = "AuthTask";
    private Context context;
    private SharedPreferences settings;
    private int pref_auth_login_count;
    private String login, password;
    private final OkHttpClient client;

    public AuthTask(Context context, @Nullable String login, @Nullable String password) {
        this.context = context;
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        pref_auth_login_count = settings.getInt("pref_auth_login_count", 3);
        this.login = login;
        this.password = password;
        this.client = new OkHttpClient();
    }

    public AuthTask(Context context) {
        this(context, null, null);
    }

    @Override
    public boolean runTask() {
        int count = 0;
        SharedPreferences preferences = App.get().getSharedPreferences();
        String login = this.login == null ? preferences.getString("auth_user", null) : this.login;
        String password = this.password == null ? preferences.getString("auth_pass", null) : this.password;
        if (login == null || password == null) {
            Intent intent = new Intent(context, LoginActivity_.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return false;
        }

        while (!isConnected() && !isInterrupt()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("redirurl", "/");
            params.put("accept", "Continue");
            params.put("auth_user", login);
            params.put("auth_pass", password);
            switchToWifiNetwork((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("redirurl", "/")
                        .add("auth_user", login)
                        .add("auth_pass", password)
                        .add("accept", "Continue")
                        .build();
                Request request = new Request.Builder()
                        .url("https://pfsense.bmstu.ru:8001")
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    onStateChange(R.string.auth_finished, 0, 0);
                    Matcher matcher = Pattern.compile("NAME=\"logout_id\" TYPE=\"hidden\" VALUE=\"([^\"]+)\">").matcher(response.body().string());
                    if (matcher.find()) {
                        settings.edit().putString("logout_id", matcher.group(1)).apply();
                        Log.i(TAG, "Авторизация прошла успешно. logout_id = " + matcher.group(1));
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                onStateChange(R.string.auth_err, count, pref_auth_login_count);
            }

            if (count++ == pref_auth_login_count) {
                onStateChange(R.string.auth_err, 0, 0);
                return false;
            }
        }
        onStateChange(R.string.auth_finished, 0, 0);
        return true;
    }

    public boolean isConnected() {
        return WiFiHelper.isConnected(context, settings.getBoolean("pref_wifi_check_strict", true));
    }


}
