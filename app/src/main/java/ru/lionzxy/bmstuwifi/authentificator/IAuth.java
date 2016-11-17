package ru.lionzxy.bmstuwifi.authentificator;

import android.content.Context;

import java.io.IOException;

import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 05.11.16.
 */

public abstract class IAuth {
    private String[] SSIDs;
    protected Logger logger;
    protected Context context;

    public IAuth(Logger logger, Context context, String... SSIDs) {
        this.SSIDs = SSIDs;
        this.logger = logger;
        this.context = context;
    }
    public abstract void stop();
    public abstract boolean registerInNetwork() throws IOException;

    public boolean isValidSSID(String SSID) {
        SSID = SSID.replaceAll("\"","");
        for (String ssid : SSIDs)
            if (ssid.equals(SSID))
                return true;
        return false;
    }
}
