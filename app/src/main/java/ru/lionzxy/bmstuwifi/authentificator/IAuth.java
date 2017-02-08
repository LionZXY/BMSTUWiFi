package ru.lionzxy.bmstuwifi.authentificator;

import ru.lionzxy.bmstuwifi.App;
import ru.lionzxy.bmstuwifi.interfaces.ITask;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 05.11.16.
 */

public abstract class IAuth {
    private String SSID;
    protected Logger logger;
    private String nameid;

    public IAuth(Logger logger, String nameid, String SSID) {
        this.SSID = SSID;
        this.logger = logger;
        this.nameid = nameid;
    }

    public abstract void stop();

    public String getNameid() {
        return nameid;
    }

    public abstract ITask registerInNetwork();
    public abstract String getAuthSite();
    public abstract String getLogoutSite();

    public String getSSID() {
        return SSID;
    }

    public boolean isValidSSID(String SSID) {
        return this.SSID.equals(SSID);
    }

    public String getLogin(String defaultV) {
        return App.get().getSharedPreferences().getString(getNameid() + "_login", defaultV);
    }

    public String getPassword(String defaultV) {
        return App.get().getSharedPreferences().getString(getNameid() + "_password", defaultV);
    }

    public String getLogoutId(String defaultV) {
        return App.get().getSharedPreferences().getString(getNameid() + "_logout", defaultV);
    }

    public IAuth setLogin(String login) {
        App.get().getSharedPreferences().edit().putString(getNameid() + "_login", login).apply();

        return this;
    }

    public IAuth setPassword(String password) {
        App.get().getSharedPreferences().edit().putString(getNameid() + "_password", password).apply();
        return this;
    }

    public IAuth setLogoutId(String logoutId) {
        App.get().getSharedPreferences().edit().putString(getNameid() + "_logout", logoutId).apply();
        return this;
    }
}
