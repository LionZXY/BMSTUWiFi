package ru.lionzxy.bmstuwifi.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import ru.lionzxy.bmstuwifi.tasks.LogoutTask;
import ru.lionzxy.bmstuwifi.tasks.interfaces.ITaskStateResponse;
import ru.lionzxy.bmstuwifi.tasks.interfaces.TaskResponseWithNotification;

/**
 * Created by lionzxy on 17.11.16.
 */

public class LogoutAsyncTaskLoader extends AsyncTaskLoader<Boolean> {
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<ProgressDialog> progressDialogWeakReference;
    private WeakReference<Notification> notificationWeakReference;
    private LogoutTask logoutTask;

    public LogoutAsyncTaskLoader(Activity activity, @Nullable ProgressDialog progressDialog, @Nullable Notification notification, String logout_id) {
        super(activity);
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.progressDialogWeakReference = new WeakReference<ProgressDialog>(progressDialog);
        this.notificationWeakReference = new WeakReference<Notification>(notification);
        this.logoutTask = new LogoutTask(logout_id);
    }

    @Override
    public Boolean loadInBackground() {
        final TaskResponseWithNotification taskResponseWithNotification = new TaskResponseWithNotification(notificationWeakReference.get());
        return logoutTask.subscribeOnStateChange(new ITaskStateResponse() {
            @Override
            public void onStateChange(final String TAG, final int stateDescribtionResId, final int stateNumber, final int stateCount) {
                activityWeakReference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (notificationWeakReference.get() != null)
                            taskResponseWithNotification.onStateChange(TAG, stateDescribtionResId, stateNumber, stateCount);
                        if (progressDialogWeakReference.get() != null) {
                            progressDialogWeakReference.get().setProgress(stateCount);
                            progressDialogWeakReference.get().setMessage(getContext().getString(stateDescribtionResId));
                            progressDialogWeakReference.get().setMax(stateCount);
                            progressDialogWeakReference.get().show();
                        }
                    }
                });
                Logger.getLogger().log(TAG, Logger.Level.INFO, activityWeakReference.get().getString(stateDescribtionResId));
            }
        }).runTask();
    }
}
