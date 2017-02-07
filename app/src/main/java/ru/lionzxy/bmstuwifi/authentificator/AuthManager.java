package ru.lionzxy.bmstuwifi.authentificator;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Set;

import ru.lionzxy.bmstuwifi.utils.WiFiHelper;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 10.01.17.
 */

public class AuthManager {
    private static HashMap<String, IAuth> authFromSSID = new HashMap<>();
    private static HashMap<String, IAuth> authFromId = new HashMap<>();

    static {
        IAuth auth = new BMSTUStudentAuth(Logger.getLogger());
        authFromId.put(auth.getNameid(), auth);
        authFromSSID.put(auth.getSSID(), auth);

        auth = new BMSTUTeacherAuth(Logger.getLogger());
        authFromId.put(auth.getNameid(), auth);
        authFromSSID.put(auth.getSSID(), auth);
    }

    public static boolean isValidSSID(String SSID) {
        SSID = SSID.replaceAll("\"", "");
        return authFromSSID.containsKey(SSID);
    }

    public static IAuth getAuthForSSID(String SSID) {
        return authFromSSID.get(SSID);
    }

    public static Set<String> getSSIDs() {
        return authFromSSID.keySet();
    }

    public static IAuth getAuthFromId(String id) {
        return authFromId.get(id);
    }

    @Nullable
    public static IAuth getCurrentAuth(Context context){
        return getAuthForSSID(WiFiHelper.getCurrentSSID(context));
    }
}
