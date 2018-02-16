/**
 * This class had been forked and it was changed for own tasks from repository "Wi-Fi в метро" (pw.thedrhax.mosmetro, Moscow Wi-Fi autologin)
 * Copyright © 2015 Dmitry Karikh <the.dr.hax@gmail.com>
 */
package ru.lionzxy.wifijob.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Util class used to monitor every change of the stored variable.
 * <p>
 * Main functions:
 * - Subscribe to already existing Listeners of the same type
 * - Allow to retrieve and change the value of variable at any time
 * - Notify about every change using the onChange() callback
 *
 * @param <T> type of the stored variable
 * @author Dmitry Karikh <the.dr.hax@gmail.com>
 */
public class Listener<T> {
    private final List<Listener<T>> callbacks = new LinkedList<>();
    private T value;

    public Listener(T initial_value) {
        value = initial_value;
    }

    public final synchronized void set(T new_value) {
        value = new_value;
        onChange(new_value);
        synchronized (callbacks) {
            for (Listener<T> callback : callbacks) {
                callback.set(new_value);
            }
        }
    }

    public final T get() {
        return value;
    }

    public void subscribe(Listener<T> master) {
        synchronized (master.callbacks) {
            master.callbacks.add(this);
        }
        this.value = master.value;
    }

    public void onChange(T new_value) {

    }
}
