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

/**
 * Created by lionzxy on 27.11.16.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.logout)
    View logoutView;
    private ProgressDialog progressDialog = null;

    @AfterViews
    public void afterViews() {
    }

    @Click(R.id.auth)
    public void onClickAuth(View v) {

    }

    @Click(R.id.settings)
    public void onClickSetting(View v) {
    }

    @Click(R.id.logout)
    public void onClickLogout(View v) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
