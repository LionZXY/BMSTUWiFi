package ru.lionzxy.bmstuwifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.lionzxy.bmstuwifi.utils.Constant;
import ru.lionzxy.bmstuwifi.utils.Notify;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 27.11.16.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.logout)
    View logoutView;
    private ProgressDialog progressDialog = null;
    private Notify notification = null;

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

        } catch (Exception e) {
            Logger.getLogger().logAboutCrash("MainActivity", e);
        }
    }

    @Click(R.id.auth)
    public void onClickAuth(View v) {

    }

    @Click(R.id.settings)
    public void onClickSetting(View v) {
        startActivity(new Intent(this, AppPreferenceActivity.class));
    }

    @Click(R.id.logout)
    public void onClickLogout(View v) {
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
                    } catch (Exception e) {
                        Logger.getLogger().logAboutCrash("Permission", e);
                    }

                } else {
                    Toast.makeText(this, R.string.toast_lwrite_permission_deny, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
