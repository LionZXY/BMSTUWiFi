package ru.lionzxy.bmstuwifi.utils.logs;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import ru.lionzxy.bmstuwifi.App;
import ru.lionzxy.bmstuwifi.interfaces.OnLogUpdate;


public class Logger implements Parcelable {
    public enum Level {
        DEBUG,
        INFO,
        ERROR
    }

    private ArrayList<OnLogUpdate> onLogUpdates = new ArrayList<>();
    private static Logger INSTANCE = new Logger();
    private HashMap<Level, ArrayList<String>> log;

    public static Logger getLogger() {
        return INSTANCE;
    }

    public Logger() {
        log = new HashMap<>();
        log.put(Level.DEBUG, new ArrayList<String>());
        log.put(Level.INFO, new ArrayList<String>());
        subscribeOnUpdate(new OnLogUpdate() {
            @Override
            public void onLogUpdate(Level level, String TAG, String log) {
                if (level == Level.INFO)
                    Log.i(TAG, log);
                if (level == Level.ERROR)
                    Log.e(TAG, log);
                Log.d(TAG, log);
            }
        });
        subscribeOnUpdate(new OnLogUpdate() {
            @Override
            public void onLogUpdate(Level level, String TAG, String log) {
                FirebaseCrash.log("[" + TAG + "] " + log);
            }
        });
    }

    public void log(String TAG, Level level, String message) {

        if (level == Level.INFO) {
            log.get(Level.INFO).add("[" + TAG + "] " + "[" + new Date(System.currentTimeMillis()) + "] " + message);
        }
        log.get(Level.DEBUG).add("[" + TAG + "] " + message);
        for (OnLogUpdate update : onLogUpdates)
            try {
                update.onLogUpdate(level, TAG, message);
            } catch (Exception e) {
                logAboutCrash(TAG, e);
                Log.e(TAG, "Ошибка в отправке события обновления лога", e);
            }
    }

    public void log(String TAG, Level level, int resId) {
        log(TAG, level, getContext().getString(resId));
    }

    //TODO
    public void logAboutCrash(String TAG, Exception e) {
        FirebaseCrash.report(e);
        Log.e(TAG, "Pizdes", e);
    }

    public ArrayList<String> getLogByLevel(Level level) {
        return log.get(level);
    }

    public void subscribeOnUpdate(OnLogUpdate onLogUpdate) {
        if (!onLogUpdates.contains(onLogUpdate))
            onLogUpdates.add(onLogUpdate);
    }

    protected Logger(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        if (bundle.getSerializable("logger") != null && bundle.getSerializable("logger") instanceof HashMap)
            log = (HashMap<Level, ArrayList<String>>) bundle.getSerializable("logger");
    }

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

}
