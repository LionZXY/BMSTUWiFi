package ru.lionzxy.bmstuwifi.utils.logs;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.lionzxy.bmstuwifi.App;
import ru.lionzxy.bmstuwifi.interfaces.OnAppTerminate;
import ru.lionzxy.bmstuwifi.interfaces.OnLogUpdate;
import ru.lionzxy.bmstuwifi.utils.FileHelper;

/**
 * Created by lionzxy on 02.02.17.
 */

public class LogFile implements OnLogUpdate, OnAppTerminate {
    private OutputStreamWriter fileStream = null;


    public LogFile() throws Exception {
        File logFile = new File(Environment.getExternalStorageDirectory() + "/bmstuwifi/",
                dateToString(new Date(System.currentTimeMillis())) + "-wifi-DEBUG.log");
        Log.i("TAG", logFile.toString());
        if (!FileHelper.createFile(logFile))
            throw new RuntimeException("Невозможно создать файл");
        fileStream = new OutputStreamWriter(new FileOutputStream(logFile));
        App.get().subcribeOnTerminate(this);
    }

    public static void init(Logger logger) throws Exception{
        logger.subscribeOnUpdate(new LogFile());
    }

    @Override
    public void onLogUpdate(Logger.Level level, String TAG, String log) {
        try {
            fileStream.write("[" + level.name() + "][" + TAG + "] " + log);
            fileStream.flush();
        } catch (Exception e) {
            Logger.getLogger().logAboutCrash("LogFile", e);
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

    @Override
    public void onTerminate() {
        try {
            fileStream.flush();
            fileStream.close();
        } catch (Exception e) {
            Logger.getLogger().logAboutCrash("LogFile", e);
        }
    }
}
