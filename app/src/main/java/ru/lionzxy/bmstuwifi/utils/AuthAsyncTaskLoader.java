package ru.lionzxy.bmstuwifi.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.lang.ref.WeakReference;

import ru.lionzxy.bmstuwifi.authentificator.AuthManager;
import ru.lionzxy.bmstuwifi.interfaces.ITaskStateResponse;
import ru.lionzxy.bmstuwifi.interfaces.TaskResponseWithNotification;
import ru.lionzxy.bmstuwifi.tasks.AuthTask;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * TODO Это очень плохое решение
 */

public class AuthAsyncTaskLoader extends AsyncTaskLoader<Boolean> {
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<ProgressDialog> progressDialogWeakReference;
    private WeakReference<Notification> notificationWeakReference;
    private AuthTask authTask = null;


    public AuthAsyncTaskLoader(Activity activity, @Nullable ProgressDialog progressDialog, @Nullable Notification notification, @NonNull Bundle args) {
        super(activity);
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.progressDialogWeakReference = new WeakReference<ProgressDialog>(progressDialog);
        this.notificationWeakReference = new WeakReference<Notification>(notification);
        authTask = (AuthTask) AuthManager.getAuthFromId(args.getString("wifi_id")).registerInNetwork();
    }

    @Override
    public Boolean loadInBackground() {
        final TaskResponseWithNotification taskResponseWithNotification = new TaskResponseWithNotification(notificationWeakReference.get());
        return authTask.subscribeOnStateChange(new ITaskStateResponse() {
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

    @Override
    public void onCanceled(Boolean data) {
        super.onCanceled(data);
        authTask.interrupt();
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        if (notificationWeakReference.get() != null)
            notificationWeakReference.get().hide();
        if (progressDialogWeakReference.get() != null)
            progressDialogWeakReference.get().dismiss();
        if(authTask != null)
            authTask.interrupt();
    }
}
