package ru.lionzxy.bmstuwifi.authentificator;

import android.content.Context;
import android.content.Intent;

import com.securepreferences.SecurePreferences;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.lionzxy.bmstuwifi.LoginActivity;
import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 05.11.16.
 */
//TODO
public class BMSTUStudentAuth extends IAuth {
    boolean alreadyActive = false;
    private static final OkHttpClient client = new OkHttpClient();

    public BMSTUStudentAuth(Logger logger, Context context) {
        super(logger, context, "bmstu_lb");
    }


    @Override
    public void stop() {
        alreadyActive = false;
    }

    @Override
    public boolean registerInNetwork() throws IOException {
        if (!alreadyActive) {
            alreadyActive = true;
            SecurePreferences securePreferences = new SecurePreferences(context);
            String login = securePreferences.getString("auth_user", null);
            String password = securePreferences.getString("auth_pass", null);

            if (login == null || password == null) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            } else {
                RequestBody formBody = new FormBody.Builder()
                        .add("redirurl", "/")
                        .add("auth_user", login)
                        .add("auth_pass", password)
                        .add("accept", "Continue")
                        .build();
                Request request = new Request.Builder()
                        .url("https://pfsense.bmstu.ru:8001/index.php")
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
            }
            return true;
        }
        return false;
    }
}
