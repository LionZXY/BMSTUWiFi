package ru.lionzxy.bmstuwifi.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import ru.lionzxy.bmstuwifi.App;


public class Logger implements Parcelable {
    public enum Level {
        DEBUG,
        INFO,
        ERROR
    }

    public interface OnLogUpdate {
        void onLogUpdate(Level level, String TAG, String log);
    }

    private ArrayList<OnLogUpdate> onLogUpdates = new ArrayList<>();
    private final static Logger INSTANCE = new Logger();
    private HashMap<Level, ArrayList<String>> log;
    private WeakReference<Context> con = null;

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
                Log.e(TAG, "Ошибка в отправке события обновления лога", e);
            }
    }

    public void log(String TAG, Level level, int resId) {
        log(TAG, level, getContext().getString(resId));
    }

    public void init(Context context) {
        this.con = new WeakReference<Context>(context);
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

    public Context getContext() {
        return con.get() == null ? App.get().getBaseContext() : con.get();
    }

    public void saveInFile() {
        for (Map.Entry<Level, ArrayList<String>> level : log.entrySet()) {
            File logFile = new File(Environment.getExternalStorageDirectory() + "/bmstuwifi/", dateToString(new Date(System.currentTimeMillis())) + "-wifi-" + level.getKey().name() + ".log");
            OutputStreamWriter streamWriter = null;
            try {
                if (logFile.createNewFile()) {
                    streamWriter = new OutputStreamWriter(new FileOutputStream(logFile));
                    for (String line : level.getValue())
                        streamWriter.write(line + "\n");
                    streamWriter.flush();
                    log.get(level.getKey()).clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (streamWriter != null)
                    try {
                        streamWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

    }

    public static String dateToString(Date date) {
        //YYYY-MM-DD HH:MM:SS
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        String dateStr = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        dateStr += "_" + calendar.get(Calendar.HOUR_OF_DAY) + "." + calendar.get(Calendar.MINUTE) + "." + calendar.get(Calendar.SECOND);
        return dateStr;
    }

}
