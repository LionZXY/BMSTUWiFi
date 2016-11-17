package ru.lionzxy.bmstuwifi.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

/**
 * Copy-paste from https://github.com/TheDrHax/mosmetro-android/blob/master/src/pw/thedrhax/util/Notification.java
 */

public class Notification {
    private android.app.Notification.Builder builder;
    private NotificationManager notificationManager;
    private Context context;
    private Resources res;

    private int id = 0;
    private boolean cancellable;
    private boolean enabled = true;

    public Notification(Context context) {
        this.builder = new android.app.Notification.Builder(context);
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.context = context;
        this.res = context.getResources();

        setCancellable(true);
    }


    public Notification setTitle(String title) {
        builder = builder.setContentTitle(title);
        return this;
    }
    public Notification setText(int resId){
        setText(res.getString(resId));
        return this;
    }
    public Notification setText(String text) {
        builder = builder.setContentText(text);

        if (Build.VERSION.SDK_INT >= 16) // show expandable text on newer devices
            builder = builder.setStyle(new android.app.Notification.BigTextStyle().bigText(text));
        return this;
    }

    public Notification setId(int id) {
        this.id = id;
        return this;
    }

    public Notification setIcon(int icon) {
        builder = builder.setSmallIcon(icon);
        return this;
    }

    public Notification setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
        builder = builder.setAutoCancel(cancellable); // delete notification on press if true
        return this;
    }

    public Notification setContinuous() {
        if (Build.VERSION.SDK_INT >= 14)
            builder = builder.setProgress(0, 0, true);
        return this;
    }
    public Notification setProgress(int progress, int maxProgress) {
        if (Build.VERSION.SDK_INT >= 14)
            builder = builder.setProgress(maxProgress, progress, false);
        return this;
    }
    public Notification setProgress(int progress) {
        if (Build.VERSION.SDK_INT >= 14)
            builder = builder.setProgress(100, progress, false);
        return this;
    }

    public Notification setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void show() {
        if (!enabled) return;

        android.app.Notification notification;
        if (Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else { // support older devices
            notification = builder.getNotification();
        }
        //if (!cancellable) notification.flags |= android.app.Notification.FLAG_NO_CLEAR;
        notificationManager.notify(id, notification);
    }

    public void hide() {
        notificationManager.cancel(id);
    }
}
