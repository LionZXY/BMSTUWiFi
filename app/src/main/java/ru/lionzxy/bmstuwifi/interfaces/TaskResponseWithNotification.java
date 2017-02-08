package ru.lionzxy.bmstuwifi.interfaces;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ru.lionzxy.bmstuwifi.App;
import ru.lionzxy.bmstuwifi.utils.Notification;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 13.11.16.
 */

public class TaskResponseWithNotification implements ITaskStateResponse, ICanOpenActivity {
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

    @Override
    public void openActivity(Class<? extends Activity> activityClass, Bundle extras, String action) {
        Intent notificationIntent = new Intent(App.get(), activityClass);
        if (extras != null)
            notificationIntent.putExtras(extras);
        if (action != null)
            notificationIntent.setAction(action);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(App.get(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.getBuilder().setContentIntent(intent);
        notification.show();
    }
}
