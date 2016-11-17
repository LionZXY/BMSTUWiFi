package ru.lionzxy.bmstuwifi.tasks.interfaces;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by lionzxy on 12.11.16.
 */

public abstract class TaskResponseWithContext implements ITaskStateResponse {
    private Resources res;

    public TaskResponseWithContext(Context context) {
        res = context.getResources();
    }

    @Override
    public void onStateChange(int stateDescribtionResId, int stateNumber, int stateCount) {
        onStateChange(res.getString(stateDescribtionResId), stateNumber, stateCount);
    }

    public abstract void onStateChange(String stateDescribtion, int stateNumber, int stateCount);
}
