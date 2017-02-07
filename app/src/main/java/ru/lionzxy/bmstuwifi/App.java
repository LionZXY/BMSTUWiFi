package ru.lionzxy.bmstuwifi;

import android.app.Application;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

/**
 * Created by lionzxy on 13.11.16.
 */

public class App extends Application {
    protected static App instance;
    private SecurePreferences mSecurePrefs;

    public App() {
        super();
        instance = this;

    }

    public static App get() {
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        if (mSecurePrefs == null) {
            mSecurePrefs = new SecurePreferences(this);
        }
        return mSecurePrefs;
    }
}
