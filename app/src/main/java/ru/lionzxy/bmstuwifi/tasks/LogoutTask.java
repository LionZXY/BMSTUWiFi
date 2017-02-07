package ru.lionzxy.bmstuwifi.tasks;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.lionzxy.bmstuwifi.R;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITaskStateResponse;
import ru.lionzxy.bmstuwifi.utils.Constant;

/**
 * Created by lionzxy on 17.11.16.
 */

public class LogoutTask extends ITask {
    private String TAG = "LogOut";
    private String logout_id;
    private final OkHttpClient client;

    public LogoutTask(String logout_id) {
        this.logout_id = logout_id;
        this.client = new OkHttpClient();
    }

    @Override
    public boolean runTask() {
        if (!isInterrupt()) {
            onStateChange(R.string.auth_logout_start, 0, ITaskStateResponse.INFINITE_STATES);

            if (logout_id != null && !logout_id.equals("")) {
                try {
                    RequestBody formBody = new FormBody.Builder()
                            .add("logout_id", logout_id)
                            .add("logout", "Logout")
                            .build();
                    Request request = new Request.Builder()
                            .url(Constant.LOGOUT_SITE)
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
        return false;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
