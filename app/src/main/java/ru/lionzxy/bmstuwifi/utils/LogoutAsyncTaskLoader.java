package ru.lionzxy.bmstuwifi.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import ru.lionzxy.bmstuwifi.authentificator.AuthManager;
import ru.lionzxy.bmstuwifi.authentificator.IAuth;
import ru.lionzxy.bmstuwifi.interfaces.ITaskStateResponse;
import ru.lionzxy.bmstuwifi.interfaces.TaskResponseWithNotification;
import ru.lionzxy.bmstuwifi.tasks.LogoutTask;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * //TODO и это плохое решение
 */

public class LogoutAsyncTaskLoader extends AsyncTaskLoader<Boolean> {
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<ProgressDialog> progressDialogWeakReference;
    private WeakReference<Notification> notificationWeakReference;
    private IAuth auth = null;

    public LogoutAsyncTaskLoader(Activity activity, @Nullable ProgressDialog progressDialog, @Nullable Notification notification) {
        super(activity);
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.progressDialogWeakReference = new WeakReference<ProgressDialog>(progressDialog);
        this.notificationWeakReference = new WeakReference<Notification>(notification);
        this.auth = AuthManager.getCurrentAuth(getContext());
    }

    @Override
    public Boolean loadInBackground() {
        final TaskResponseWithNotification taskResponseWithNotification = new TaskResponseWithNotification(notificationWeakReference.get());
        return new LogoutTask(auth).subscribeOnStateChange(new ITaskStateResponse() {
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
                Logger.getLogger().log(TAG, Logger.Level.INFO, getContext().getString(stateDescribtionResId));
            }
        }).runTask();
    }

    public IAuth getAuth() {
        return auth;
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        if (notificationWeakReference.get() != null)
            notificationWeakReference.get().hide();
        if(progressDialogWeakReference.get() != null)
            progressDialogWeakReference.get().dismiss();

    }


}
