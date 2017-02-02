package ru.lionzxy.bmstuwifi.authentificator;

import java.io.IOException;

import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 05.11.16.
 */

public abstract class IAuth{
    private String[] SSIDs;
    protected Logger logger;

    public IAuth(Logger logger, String... SSIDs) {
        this.SSIDs = SSIDs;
        this.logger = logger;
    }

    public abstract void stop();

    public abstract ITask registerInNetwork() throws IOException;

    public String[] getSSIDs() {
        return SSIDs;
    }

    public boolean isValidSSID(String SSID) {
        SSID = SSID.replaceAll("\"", "");
        for (String ssid : SSIDs)
            if (ssid.equals(SSID))
                return true;
        return false;
    }
}
