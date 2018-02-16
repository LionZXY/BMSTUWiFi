package ru.lionzxy.bmstuwifi;

import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.securepreferences.SecurePreferences;

import ru.companion.lionzxy.wifijob.WiFiJob;
import ru.lionzxy.bmstuwifi.provider.BMSTUStudentAuth;

/**
 * Created by lionzxy on 13.11.16.
 */

public class App extends MultiDexApplication {
    protected static App instance;

    static {
        WiFiJob.addProvider("bmstu_lb", BMSTUStudentAuth.class);
    }

    private SecurePreferences mSecurePrefs;
    private FirebaseAnalytics mFirebaseAnalytics;

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
    }
}
