package ru.lionzxy.bmstuwifi;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.lionzxy.bmstuwifi.utils.AuthAsyncTaskLoader;
import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 25.11.16.
 */

@EActivity(R.layout.log_activity)
public class DebugActivity extends AppCompatActivity implements Logger.OnLogUpdate, LoaderManager.LoaderCallbacks<Boolean> {
    private Logger.Level level = Logger.Level.INFO;
    private Logger logger = Logger.getLogger();
    private ProgressDialog progressDialog;

    @ViewById(R.id.text_messages)
    TextView logText;
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
    }

    @Click(R.id.button_connect)
    public void onClickConnect() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(R.string.auth);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.login_button_hide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });


        SecurePreferences securePreferences = new SecurePreferences(this);
        String login = securePreferences.getString("auth_user", null);
        String password = securePreferences.getString("auth_pass", null);

        if (login == null || password == null) {
            Toast.makeText(this, R.string.debug_login_no_login, Toast.LENGTH_LONG).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("auth_user", login);
        bundle.putString("auth_pass", password);

        getSupportLoaderManager().initLoader(0, bundle, this);
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

}
