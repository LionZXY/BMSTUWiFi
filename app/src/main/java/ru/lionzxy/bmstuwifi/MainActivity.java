package ru.lionzxy.bmstuwifi;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by lionzxy on 27.11.16.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.version)
    TextView version;

    @AfterViews
    public void afterViews() {
        try {
            version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.auth)
    public void onClickAuth() {
        Intent intent = new Intent(this, LoginActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.settings)
    public void onClickSetting() {
        Intent intent = new Intent(this, AppPreferenceActivity.class);
        startActivity(intent);
    }
}
