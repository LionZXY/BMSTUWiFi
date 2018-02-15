package ru.lionzxy.bmstuwifi.utils.logs;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.HashMap;

import ru.lionzxy.bmstuwifi.App;


public class Logger implements Parcelable {
    public static final Creator<Logger> CREATOR = new Creator<Logger>() {
        @Override
        public Logger createFromParcel(Parcel in) {

            return new Logger(in);
        }

        @Override
        public Logger[] newArray(int size) {
            return new Logger[size];
        }
    };
    private static Logger INSTANCE = new Logger();
    private HashMap<Level, ArrayList<String>> log;

    public Logger() {
        log = new HashMap<>();
        log.put(Level.DEBUG, new ArrayList<String>());
        log.put(Level.INFO, new ArrayList<String>());

    }

    protected Logger(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        if (bundle.getSerializable("logger") != null && bundle.getSerializable("logger") instanceof HashMap)
            log = (HashMap<Level, ArrayList<String>>) bundle.getSerializable("logger");
    }

    public static Logger getLogger() {
        return INSTANCE;
    }

    public static void log(Object clazz, String log) {
        getLogger().log(clazz.getClass().getSimpleName(), Level.INFO, log);
    }

    public static void log(String log) {
        getLogger().log("UNKNOWN", Level.INFO, log);
    }

    public void log(String TAG, Level level, String message) {

        if (level == Level.INFO) {
            log.get(Level.INFO).add("[" + TAG + "] " + "[" + System.currentTimeMillis() + "] " + message);
        }
        log.get(Level.DEBUG).add("[" + TAG + "] " + message);
        Log.d(TAG, message);

    }

    public void log(String TAG, Level level, int resId) {
        log(TAG, level, getContext().getString(resId));
    }

    public void logAboutCrash(String TAG, Exception e) {
        FirebaseCrash.report(e);
        Log.e(TAG, "Pizdes", e);
    }

    public ArrayList<String> getLogByLevel(Level level) {
        return log.get(level);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("logger", log);
        dest.writeBundle(bundle);
    }

    public Context getContext() {
        return App.get().getBaseContext();
    }

    public enum Level {
        DEBUG,
        INFO,
        ERROR
    }

}
