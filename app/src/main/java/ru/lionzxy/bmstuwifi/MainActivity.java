package ru.lionzxy.bmstuwifi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;
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

    @Click(R.id.auth)
    public void onClickAuth(View v) {
        startActivity(new Intent(this, LoginActivity_.class));

    }

    @Click(R.id.settings)
    public void onClickSetting(View v) {
        startActivity(new Intent(this, AppPreferenceActivity.class));
    }

    @Click(R.id.logout)
    public void onClickAbout(View v) {
        Toast.makeText(this, "Временно недоступно", Toast.LENGTH_LONG).show();
    }
}
