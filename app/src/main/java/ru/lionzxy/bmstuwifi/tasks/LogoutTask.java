package ru.lionzxy.bmstuwifi.tasks;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.lionzxy.bmstuwifi.R;
import ru.lionzxy.bmstuwifi.authentificator.IAuth;
import ru.lionzxy.bmstuwifi.interfaces.ITask;
import ru.lionzxy.bmstuwifi.interfaces.ITaskStateResponse;

/**
 * Created by lionzxy on 17.11.16.
 */

public class LogoutTask extends ITask {
    private static final String TAG = "LogOut";
    private IAuth auth;
    private final OkHttpClient client;

    public LogoutTask(IAuth auth) {
        this.auth = auth;
        this.client = new OkHttpClient();
    }

    @Override
    public boolean runTask() {
        if (!isInterrupt()) {
            onStateChange(R.string.auth_logout_start, 0, ITaskStateResponse.INFINITE_STATES);
            if (auth == null) {
                onStateChange(R.string.auth_logout_error_ssid);
                return false;
            } else {
                if (auth.getLogoutId(null) != null && !auth.getLogoutId(null).equals("")) {
                    try {
                        RequestBody formBody = new FormBody.Builder()
                                .add("logout_id", auth.getLogoutId(null))
                                .add("logout", "Logout")
                                .build();
                        Request request = new Request.Builder()
                                .url(auth.getLogoutSite())
                                .post(formBody)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            onStateChange(R.string.auth_logout_end);
                            return true;
                        }
                    } catch (IOException ex) {
                        onStateChange(R.string.auth_logout_error);
                        return false;
                    }
                } else {
                    onStateChange(R.string.auth_logout_error_session);
                    return false;
                }

                onStateChange(R.string.auth_logout_end);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
