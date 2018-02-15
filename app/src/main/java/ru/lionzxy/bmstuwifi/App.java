package ru.lionzxy.bmstuwifi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.securepreferences.SecurePreferences;

/**
 * Created by lionzxy on 13.11.16.
 */

public class App extends MultiDexApplication {
    //TODO private HashMap<Class<? extends ITask>, ITask> taskHashMap = new HashMap<>();
    protected static App instance;
    private SecurePreferences mSecurePrefs;
    private FirebaseAnalytics mFirebaseAnalytics;

    public App() {
        instance = this;
    }

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
    }

    public SharedPreferences getSharedPreferences() {
        if (mSecurePrefs == null) {
            mSecurePrefs = new SecurePreferences(this);
        }

        return mSecurePrefs;
    }
}
