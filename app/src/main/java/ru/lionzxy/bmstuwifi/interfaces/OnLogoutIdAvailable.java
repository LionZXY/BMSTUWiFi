package ru.lionzxy.bmstuwifi.interfaces;

import ru.lionzxy.bmstuwifi.authentificator.IAuth;

/**
 * Created by lionzxy on 08.02.17.
 */

public interface OnLogoutIdAvailable {
    /**
     * This method can be run in background!
     */
    void logoutIdAvailable(IAuth auth);
}
