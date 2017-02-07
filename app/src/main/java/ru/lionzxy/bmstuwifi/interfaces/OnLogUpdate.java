package ru.lionzxy.bmstuwifi.interfaces;

import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 02.02.17.
 */

public interface OnLogUpdate {
    void onLogUpdate(Logger.Level level, String TAG, String log);
}

