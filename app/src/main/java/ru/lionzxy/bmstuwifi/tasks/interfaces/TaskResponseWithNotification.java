package ru.lionzxy.bmstuwifi.tasks.interfaces;

import ru.lionzxy.bmstuwifi.utils.Logger;
import ru.lionzxy.bmstuwifi.utils.Notification;

/**
 * Created by lionzxy on 13.11.16.
 */

public class TaskResponseWithNotification implements ITaskStateResponse {
    private Notification notification;

    public TaskResponseWithNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void onStateChange(String TAG, int stateDescribtionResId, int stateNumber, int stateCount) {
        Logger.getLogger().log(TAG, Logger.Level.INFO, notification.getRes().getString(stateDescribtionResId));

        if (stateNumber == ERROR_STATE)
            notification.setText(stateDescribtionResId).show();
        else {
            notification.setText(stateDescribtionResId);
            if (stateCount == INFINITE_STATES)
                notification.setContinuous();
            else notification.setProgress(stateNumber, stateCount);
            notification.show();
        }
    }
}
