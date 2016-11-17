package ru.lionzxy.bmstuwifi.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by lionzxy on 17.11.16.
 */

public class LogoutAsyncTaskLoader extends AsyncTaskLoader<Boolean> {
    SharedPreferences preferences;

    public LogoutAsyncTaskLoader(Context context) {
        super(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public Boolean loadInBackground() {
        //TODO Logout
        return null;
    }
}
