package ru.lionzxy.bmstuwifi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.securepreferences.SecurePreferences;

import java.util.HashMap;

import ru.lionzxy.bmstuwifi.tasks.AuthTask;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;
import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 13.11.16.
 */

public class App extends MultiDexApplication {
    private HashMap<Class<? extends ITask>, ITask> taskHashMap = new HashMap<>();
    protected static App instance;
    private SecurePreferences mSecurePrefs;
    private FirebaseAnalytics mFirebaseAnalytics;

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        taskHashMap.put(AuthTask.class, new AuthTask(getBaseContext()));

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

        mSecurePrefs.edit().putString("lb_auth_pass", mSecurePrefs.getString("auth_pass", null))
                .putString("lb_auth_user", mSecurePrefs.getString("auth_user", null)).apply(); //Подготовка к будущему апдейту


        return mSecurePrefs;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.getLogger().saveInFile();
    }
}
