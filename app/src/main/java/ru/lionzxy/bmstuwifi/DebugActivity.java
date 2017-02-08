package ru.lionzxy.bmstuwifi;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.lionzxy.bmstuwifi.authentificator.AuthManager;
import ru.lionzxy.bmstuwifi.interfaces.OnLogUpdate;
import ru.lionzxy.bmstuwifi.services.ConnectionService;
import ru.lionzxy.bmstuwifi.utils.AuthAsyncTaskLoader;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 25.11.16.
 */

@EActivity(R.layout.log_activity)
public class DebugActivity extends AppCompatActivity implements OnLogUpdate, LoaderManager.LoaderCallbacks<Boolean> {
    private Logger.Level level = Logger.Level.INFO;
    private Logger logger = Logger.getLogger();
    private ProgressDialog progressDialog;

    @ViewById(R.id.scrollView)
    ScrollView scrollView;

    @ViewById(R.id.text_messages)
    TextView logText;

    @ViewById(R.id.logoutIdText)
    TextView logoutId;

    @ViewById(R.id.show_debug_log)
    CheckBox checkBox;

    @AfterViews
    protected void afterViews() {
        level = checkBox.isChecked() ? Logger.Level.DEBUG : Logger.Level.INFO;

        StringBuilder stringBuilder = new StringBuilder();
        for (String logString : logger.getLogByLevel(level))
            stringBuilder.append(logString).append("\n");
        logText.setText(stringBuilder.toString());

        logger.subscribeOnUpdate(this);
        updateLogoutId();
    }

    @Click(R.id.button_connect)
    public void onClickConnect() {
        Intent service = new Intent(this, ConnectionService.class);
        startService(service);
        Toast.makeText(this, R.string.debug_retry_service, Toast.LENGTH_LONG).show();
        updateLogoutId();
    }

    @CheckedChange(R.id.show_debug_log)
    void checkedChangeOnCheckBox(CompoundButton checkbox, boolean isChecked) {
        if (isChecked)
            level = Logger.Level.DEBUG;
        else level = Logger.Level.INFO;

        StringBuilder stringBuilder = new StringBuilder();
        for (String logString : logger.getLogByLevel(level))
            stringBuilder.append(logString).append("\n");
        logText.setText(stringBuilder.toString());
    }

    @Override
    public void onLogUpdate(final Logger.Level level, final String TAG, final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DebugActivity.this.level == Logger.Level.DEBUG || DebugActivity.this.level == level)
                    logText.append("[" + TAG + "] " + log + "\n");
                updateLogoutId();
            }
        });
    }

    @Override
    public android.support.v4.content.Loader<Boolean> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader<Boolean> asyncTaskLoader = new AuthAsyncTaskLoader(this, progressDialog, null, args);
        asyncTaskLoader.forceLoad();
        return asyncTaskLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Boolean> loader, Boolean data) {
        progressDialog.setMessage(getString(R.string.auth_finished));
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
    }

    public void updateLogoutId() {
        String tmpLogout;
        StringBuilder tmpBuilder = new StringBuilder();
        for (String ssid : AuthManager.getSSIDs()) {
            tmpLogout = AuthManager.getAuthForSSID(ssid).getLogoutId(null);
            if (tmpLogout != null)
                tmpBuilder.append(AuthManager.getAuthForSSID(ssid).getNameid()).append("_logout: ").append(tmpLogout).append('\n');
        }
        logoutId.setText(tmpBuilder.toString());
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

}
