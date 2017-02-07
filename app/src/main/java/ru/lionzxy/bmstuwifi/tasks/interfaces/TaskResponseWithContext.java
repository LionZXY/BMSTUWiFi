package ru.lionzxy.bmstuwifi.tasks.interfaces;

import android.content.Context;
import android.content.res.Resources;

import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 12.11.16.
 */

public abstract class TaskResponseWithContext implements ITaskStateResponse {
    private Resources res;

    public TaskResponseWithContext(Context context) {
        res = context.getResources();
    }

    @Override
    public void onStateChange(String TAG, int stateDescribtionResId, int stateNumber, int stateCount) {
        onStateChange(res.getString(stateDescribtionResId), stateNumber, stateCount);
        Logger.getLogger().log(TAG, Logger.Level.INFO, res.getString(stateDescribtionResId));
    }

    public abstract void onStateChange(String stateDescribtion, int stateNumber, int stateCount);
}
