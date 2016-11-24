package ru.lionzxy.bmstuwifi.utils;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


public class Logger implements Parcelable {
    public enum Level {
        DEBUG,
        INFO,
        ERROR;
    }

    public interface OnLogUpdate {
        void onLogUpdate(Level level, String TAG, String log);
    }

    private ArrayList<OnLogUpdate> onLogUpdates = new ArrayList<>();
    private final static Logger INSTANCE = new Logger();
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
    }

    public void log(String TAG, Level level, String message) {
        if (level == Level.INFO) {
            log.get(Level.INFO).add("[" + TAG + "] " + message);
        }
        log.get(Level.DEBUG).add("[" + TAG + "] " + message);
        for (OnLogUpdate update : onLogUpdates)
            try {
                update.onLogUpdate(level, TAG, message);
            } catch (Exception e) {
                Log.e(TAG, "Ошибка в отправке события обновления лога", e);
            }
    }

    //TODO
    public void logAboutCrash(String TAG, Exception e) {

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
}
