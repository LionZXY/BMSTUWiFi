package ru.lionzxy.bmstuwifi.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by lionzxy on 23.11.16.
 */

public class Constant {
    public static final String REG_EXP = "<input name=\"logout_id\" type=\"hidden\" value=\"([^\"]+)\">";
    public static final String STUDENT_AUTH_SITE = "https://lbpfs.bmstu.ru:8003/index.php?zone=bmstu_lb";
    public static final String TEACHER_AUTH_SITE = "https://lbpfs.bmstu.ru:8003/index.php?zone=bmstu_stuff";
    public static final String LOGOUT_SITE = "https://lbpfs.bmstu.ru:8003/index.php?zone=bmstu_lb";

    public static String getAuthSite(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("authIsStudent", true))
            return STUDENT_AUTH_SITE;
        else return TEACHER_AUTH_SITE;
    }
}
