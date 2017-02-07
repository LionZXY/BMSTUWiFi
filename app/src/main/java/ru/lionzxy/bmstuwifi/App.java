package ru.lionzxy.bmstuwifi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.List;

import ru.lionzxy.bmstuwifi.interfaces.OnAppTerminate;

/**
 * Created by lionzxy on 13.11.16.
 */

public class App extends MultiDexApplication {
    //TODO private HashMap<Class<? extends ITask>, ITask> taskHashMap = new HashMap<>();
    protected static App instance;
    private SecurePreferences mSecurePrefs;
    private FirebaseAnalytics mFirebaseAnalytics;
    private List<OnAppTerminate> subscribeList = new ArrayList<>();

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
    }

    public static App get() {
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        if (mSecurePrefs == null) {
            mSecurePrefs = new SecurePreferences(this);
        }

        mSecurePrefs.edit().putString("lb_login", mSecurePrefs.getString("auth_pass", null))
                .putString("lb_password", mSecurePrefs.getString("auth_user", null)).apply(); //Подготовка к будущему апдейту


        return mSecurePrefs;
    }

    public void subcribeOnTerminate(OnAppTerminate listener) {
        subscribeList.add(listener);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (OnAppTerminate terminate : subscribeList)
            terminate.onTerminate();
    }
}
