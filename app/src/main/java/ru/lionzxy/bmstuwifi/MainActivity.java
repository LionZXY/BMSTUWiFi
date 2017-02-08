package ru.lionzxy.bmstuwifi;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.lionzxy.bmstuwifi.authentificator.AuthManager;
import ru.lionzxy.bmstuwifi.authentificator.IAuth;
import ru.lionzxy.bmstuwifi.fragments.SSIDChoiseFragment;
import ru.lionzxy.bmstuwifi.interfaces.OnLogoutIdAvailable;
import ru.lionzxy.bmstuwifi.tasks.LogoutTask;
import ru.lionzxy.bmstuwifi.utils.Constant;
import ru.lionzxy.bmstuwifi.utils.LogoutAsyncTaskLoader;
import ru.lionzxy.bmstuwifi.utils.Notification;
import ru.lionzxy.bmstuwifi.utils.WiFiHelper;
import ru.lionzxy.bmstuwifi.utils.logs.LogFile;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 27.11.16.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean>, OnLogoutIdAvailable {
    private ProgressDialog progressDialog = null;
    private Notification notification = null;
    @ViewById(R.id.logout)
    View logoutView;

    @AfterViews
    public void afterViews() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.REQUEST_WRITE_FOLDER);
        } else try {
            LogFile.init(Logger.getLogger());
        } catch (Exception e) {
            Logger.getLogger().logAboutCrash("MainActivity", e);
        }
        IAuth auth = AuthManager.getAuthForSSID(WiFiHelper.getCurrentSSID(this));
        if (auth != null && auth.getLogoutId(null) != null)
            logoutView.setVisibility(View.VISIBLE);

        LogoutTask.subscribeListOnLogoutAvailable(this);
    }

    @Click(R.id.auth)
    public void onClickAuth(View v) {
        new SSIDChoiseFragment().show(getFragmentManager(), "SSIDChoise");

    }

    @Click(R.id.settings)
    public void onClickSetting(View v) {
        startActivity(new Intent(this, AppPreferenceActivity.class));
    }

    @Click(R.id.logout)
    public void onClickLogout(View v) {
        if (AuthManager.getAuthForSSID(WiFiHelper.getCurrentSSID(this)) == null) {
            Toast.makeText(this, R.string.auth_logout_error_ssid, Toast.LENGTH_LONG).show();
        } else {
            Bundle bundle = new Bundle();
            getLoaderManager().initLoader(1, bundle, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (notification != null)
            notification.hide();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constant.REQUEST_WRITE_FOLDER: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        LogFile.init(Logger.getLogger());
                    } catch (Exception e) {
                        Logger.getLogger().logAboutCrash("Permission", e);
                    }

                } else {
                    Toast.makeText(this, R.string.toast_lwrite_permission_deny, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        notification = new Notification(this)
                .setId(1)
                .setEnabled(true)
                .setTitle(getString(R.string.auth_logout))
                .setIcon(R.drawable.ic_stat_logo);

        notification.show();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(R.string.auth_logout);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.login_button_hide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        switch (id) {
            case 1: {
                AsyncTaskLoader<Boolean> asyncTaskLoader = new LogoutAsyncTaskLoader(this, progressDialog, notification);
                asyncTaskLoader.forceLoad();
                return asyncTaskLoader;
            }
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(android.content.Loader<Boolean> loader, Boolean data) {
        if (data && loader instanceof LogoutAsyncTaskLoader) {
            ((LogoutAsyncTaskLoader) loader).getAuth().setLogoutId(null);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Boolean> loader) {

    }

    @Override
    public void logoutIdAvailable(IAuth auth) {
        if(auth != null && AuthManager.getCurrentAuth(getBaseContext()) == auth)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logoutView.setVisibility(View.VISIBLE);
                }
            });
    }
}
