package ru.lionzxy.bmstuwifi.authentificator;

import java.util.HashMap;

import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 10.01.17.
 */

public class AuthManager {
    public static HashMap<String, IAuth> auths = new HashMap<>();

    static {
        IAuth auth = new BMSTUStudentAuth(Logger.getLogger());
        for (String SSID : auth.getSSIDs())
            auths.put(SSID, auth);
    }

    public boolean isValidSSID(String SSID) {
        SSID = SSID.replaceAll("\"", "");
        return auths.containsKey(SSID);
    }
}
