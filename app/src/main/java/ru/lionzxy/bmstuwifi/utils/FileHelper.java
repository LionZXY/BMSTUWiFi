package ru.lionzxy.bmstuwifi.utils;

import java.io.File;
import java.io.IOException;

import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 02.02.17.
 */

public class FileHelper {
    public static String TAG = "FileHelper";

    public static void createDirectory(File f) throws IOException {
        boolean success = true;
        if (f.exists() && !f.isDirectory()) f.delete();
        if (!f.exists()) success = f.mkdirs();
        if (!success) throw new IOException("Cannot create given directory");
    }

    public static void createDirectory(String f) throws IOException {
        createDirectory(new File(f));
    }

    public static boolean createFile(File file) {
        if (!file.exists()) {
            Logger.getLogger().log(TAG, Logger.Level.DEBUG, "File " + file.getAbsolutePath() + " not exist. Create file...");
            try {
                if (file.getParentFile().exists() || file.getParentFile().mkdirs())
                    if (file.createNewFile())
                        Logger.getLogger().log(TAG, Logger.Level.DEBUG, "File created!");
                    else {
                        Logger.getLogger().log(TAG, Logger.Level.INFO, "Error create file!");
                        return false;
                    }
                else Logger.getLogger().log(TAG, Logger.Level.INFO, "Error create parent folder!");
            } catch (Exception e) {
                Logger.getLogger().logAboutCrash(TAG, e);
                return false;
            }
            return true;
        }
        return true;
    }
}
