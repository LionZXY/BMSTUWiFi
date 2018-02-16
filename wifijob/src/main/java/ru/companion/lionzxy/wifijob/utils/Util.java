package ru.companion.lionzxy.wifijob.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Util {
    public static int getIntPreference(Context context, String name, int def_value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return Integer.parseInt(settings.getString(name, Integer.valueOf(def_value).toString()));
        } catch (NumberFormatException | ClassCastException ignored) {
        }

        try {
            return settings.getInt(name, def_value);
        } catch (ClassCastException ignored) {
        }

        return def_value;
    }
}
