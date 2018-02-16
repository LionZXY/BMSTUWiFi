/**
 * This class had been forked and it was changed for own tasks from repository "Wi-Fi в метро" (pw.thedrhax.mosmetro, Moscow Wi-Fi autologin)
 * Copyright © 2015 Dmitry Karikh <the.dr.hax@gmail.com>
 */

package ru.companion.lionzxy.wifijob.services

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.SystemClock
import android.preference.PreferenceManager
import ru.companion.lionzxy.wifijob.R
import ru.companion.lionzxy.wifijob.authentificator.Provider
import ru.companion.lionzxy.wifijob.utils.*
import java.util.concurrent.locks.ReentrantLock


class ConnectionService : IntentService("ConnectionService") {
    private var from_shortcut = false
    var isFromDebug = false
        private set

    // Preferences
    private lateinit var wifi: WifiUtils
    private lateinit var settings: SharedPreferences
    private var pref_retry_count: Int = 0
    private var pref_ip_wait: Int = 0
    private var pref_notify_foreground: Boolean = false

    // Notifications
    private lateinit var notify: Notify

    // Authenticator
    private lateinit var provider: Provider

    override fun onCreate() {
        super.onCreate()

        wifi = object : WifiUtils(this) {
            override fun isConnected(SSID: String): Boolean {
                return from_shortcut || super.isConnected(SSID)
            }
        }
        settings = PreferenceManager.getDefaultSharedPreferences(this)
        pref_retry_count = Util.getIntPreference(this, "pref_retry_count", 3)
        pref_ip_wait = Util.getIntPreference(this, "pref_ip_wait", 0)
        pref_notify_foreground = settings.getBoolean("pref_notify_foreground", true)

        val stop_intent = PendingIntent.getService(
                this, 0,
                Intent(this, ConnectionService::class.java).setAction("STOP"),
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        notify = object : Notify(this) {
            override fun locked(locked: Boolean): Notify {
                // Show STOP action only if notification is locked
                if (locked) {
                    if (notify.mActions.size == 0)
                        notify.addAction(getString(R.string.stop), stop_intent)
                } else {
                    while (notify.mActions.size > 0) notify.mActions.removeAt(0)
                }
                return super.locked(locked)
            }
        }

        notify.id(1) // TODO DebugActivity
                //.onClick(PendingIntent.getActivity(this, 1,
                //        Intent(this, DebugActivity_::class.java),
                //        PendingIntent.FLAG_UPDATE_CURRENT
                //))
                .onDelete(stop_intent)
                .locked(pref_notify_foreground)
    }

    private fun notify(result: Provider.RESULT) {
        notify.hideProgress()

        when (result) {
            Provider.RESULT.CONNECTED, Provider.RESULT.ALREADY_CONNECTED -> {
                if (!pref_notify_foreground && !settings.getBoolean("pref_notify_success", true)) {
                    notify.hide()
                    return
                }

                if (settings.getBoolean("pref_notify_success_lock", true)) {
                    notify.locked(true)
                }

                notify.title(getString(R.string.notification_success))
                        .text(getString(R.string.notification_success_log))
                        .icon(R.drawable.wifijob_notification_logo,
                                R.drawable.wifijob_notification_logo)
                        .show()
            }

            Provider.RESULT.NOT_REGISTERED -> notify.hide()
                    .title(getString(R.string.notification_not_registered))
                    .text(getString(R.string.notification_not_registered_register))
                    .icon(R.drawable.wifijob_notification_logo,
                            R.drawable.wifijob_notification_logo)
                    // TODO DebugActivity
                    //.onClick(PendingIntent.getActivity(this, 0,
                    //        Intent(this, DebugActivity_::class.java)
                    //                .putExtra("data", "http://wi-fi.ru"),
                    //        PendingIntent.FLAG_UPDATE_CURRENT))
                    .id(2).locked(false).show()

            Provider.RESULT.ERROR -> notify.hide()
                    .title(getString(R.string.notification_error))
                    .text(getString(R.string.notification_error_log))
                    .icon(R.drawable.wifijob_notification_logo,
                            R.drawable.wifijob_notification_logo)
                    .enabled(!isFromDebug && settings.getBoolean("pref_notify_fail", false))
                    .id(2).locked(false).show()

            Provider.RESULT.NOT_SUPPORTED -> notify.hide()
                    .title(getString(R.string.notification_unsupported))
                    .text(getString(R.string.notification_error_log))
                    .icon(R.drawable.wifijob_notification_logo,
                            R.drawable.wifijob_notification_logo)
                    .enabled(!isFromDebug && settings.getBoolean("pref_notify_fail", false))
                    .id(2).locked(false).show()
        }

        notify.id(1) // return to defaults
                .locked(pref_notify_foreground)
                .enabled(!from_shortcut)
    }

    private fun waitForIP(): Boolean {
        if (from_shortcut) return true

        var count = 0

        Logger.log(getString(R.string.notification_ip_waiting))
        notify.title(getString(R.string.notification_ip_waiting))
                .progress(0, true)
                .show()

        while (wifi.ip == 0) {
            SystemClock.sleep(1000)

            if (pref_ip_wait != 0 && count++ == pref_ip_wait) {
                Logger.log(getString(R.string.error,
                        getString(R.string.notification_ip_wait_result,
                                " " + getString(R.string.not), pref_ip_wait
                        )
                ))
                return false
            }

            if (!running.get()) return false
        }

        Logger.log(getString(R.string.notification_ip_wait_result, "", count / 2))
        return true
    }

    private fun connect(): Provider.RESULT {
        var result: Provider.RESULT
        var count = 0

        do {
            if (count > 0) {
                notify.text(String.format("%s (%s)",
                        getString(R.string.notification_progress_waiting),
                        getString(R.string.notification_try_out_of, count + 1, pref_retry_count)
                ))
                        .progress(0, true)
                        .show()

                SystemClock.sleep((Util.getIntPreference(this, "pref_retry_delay", 5) * 1000).toLong())
            }

            result = provider.start()

            if (result === Provider.RESULT.NOT_REGISTERED) break
            if (from_shortcut) break
        } while (count.inc() < pref_retry_count && running.get() && result === Provider.RESULT.ERROR)

        return result
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        if ("STOP" == intent.action) { // Stop by intent
            Logger.log(this, "Stopping by Intent")
            running.set(false)
            return START_NOT_STICKY
        }

        when {
            intent.getBooleanExtra("debug", false) -> {
                Logger.log(this, "Started from DebugActivity")
                from_shortcut = true
                isFromDebug = true
                notify.enabled(false)
            }
            intent.getBooleanExtra("force", false) -> {
                Logger.log(this, "Started from shortcut")
                from_shortcut = true
                isFromDebug = false
                notify.enabled(true)
            }
            else -> {
                Logger.log(this, "Started by system")
                from_shortcut = false
                isFromDebug = false
                notify.enabled(true)
            }
        }
        SSID = wifi.getSSID(intent)

        if (!running.get() && lock.isLocked) {
            // Service is shutting down. Trying to interrupt
            running.set(true)
        }

        if (!running.get() && !lock.isLocked)
        // Ignore if service is already running
            if (WifiUtils.UNKNOWN_SSID != SSID || from_shortcut)
                if (Provider.isSSIDSupported(SSID) || from_shortcut)
                    onStart(intent, startId)

        return START_NOT_STICKY
    }

    public override fun onHandleIntent(intent: Intent?) {
        if (lock.tryLock()) {
            Logger.log(this, "Broadcast | ConnectionService (RUNNING = true)")
            sendBroadcast(Intent("ru.companion.lionzxy.wifijob.event.ConnectionService")
                    .putExtra("RUNNING", true)
            )

            running.set(true)
            var first_iteration = true
            while (running.get()) {
                if (!first_iteration) {
                    Logger.log(this, "Still alive!")
                } else {
                    first_iteration = false
                }

                main()
            }
            lock.unlock()

            notify.hide()
            if (from_shortcut) {
                // TODO: Do not start connection in DebugActivity after click on this notification
                notify.cancelOnClick(true).locked(false).show()
            }

            Logger.log(this, "Broadcast | ConnectionService (RUNNING = false)")
            sendBroadcast(Intent("ru.companion.lionzxy.wifijob.event.ConnectionService")
                    .putExtra("RUNNING", false)
            )
        } else {
            Logger.log(this, "Already running")
        }
    }

    private fun main() {
        notify.icon(R.drawable.wifijob_notification_logo,
                R.drawable.wifijob_notification_logo)

        // Wait for IP before detecting the Provider
        if (!waitForIP()) {
            if (running.get()) {
                notify(Provider.RESULT.ERROR)
                running.set(false)
            }
            return
        }

        Notify(this).id(2).hide() // hide error notification

        notify.title(getString(R.string.notification_auth_connecting, SSID))
                .text(getString(R.string.notification_progress_waiting))
                .progress(0, true)
                .show()

        provider = Provider.find(this, SSID)
                .setRunningListener(running)
                .setCallback(object : Provider.ICallback {
                    override fun onProgressUpdate(progress: Int) {
                        notify.progress(progress).show()
                    }

                    override fun onProgressUpdate(progress: Int, message: String) {
                        notify.text(message).progress(progress).show()
                    }
                })

        // Try to connect
        Logger.log(getString(R.string.notification_algorithm_name, provider.name))
        val result = connect()

        // Notify user if not interrupted
        if (running.get()) {
            notify(result)
        } else {
            return
        }

        // Stop the service if connection were unsuccessful or started from shortcut
        when (result) {
            Provider.RESULT.CONNECTED, Provider.RESULT.ALREADY_CONNECTED -> {
                if (Build.VERSION.SDK_INT >= 21) wifi.report(true)
                if (from_shortcut) {
                    Logger.log(this, "Stopping by result (" + result.name + ")")
                    running.set(false)
                    return
                }
            }
            else -> {
                Logger.log(this, "Stopping by result (" + result.name + ")")
                running.set(false)
                return
            }
        }

        Logger.log(this, "Broadcast | CONNECTED")
        sendBroadcast(Intent("ru.companion.lionzxy.wifijob.event.CONNECTED")
                .putExtra("SSID", SSID)
                .putExtra("PROVIDER", provider.name)
        )

        // Wait while internet connection is available
        var count = 0
        while (running.get()) {
            SystemClock.sleep(1000)

            // Check internet connection each 10 seconds
            val check_interval = Util.getIntPreference(this, "pref_internet_check_interval", 10)
            if (settings.getBoolean("pref_internet_check", true) && ++count == check_interval) {
                Logger.log(this, "Checking internet connection")
                count = 0
                if (!provider.isConnected)
                    break
            }
        }

        Logger.log(this, "Broadcast | DISCONNECTED")
        sendBroadcast(Intent("ru.companion.lionzxy.wifijob.event.DISCONNECTED"))
        notify.hide()

        // Try to reconnect the Wi-Fi network
        if (settings.getBoolean("pref_wifi_reconnect", false)) {
            Logger.log(this, "Reconnecting to Wi-Fi")
            wifi.reconnect(SSID)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Logger.log(this, "onTaskRemoved()")
        if (!settings.getBoolean("pref_notify_foreground", true)) {
            Logger.log("Stopping because of task removal")
            running.set(false)
        }
    }

    companion object {
        private val lock = ReentrantLock()
        private val running = Listener(false)
        private var SSID = WifiUtils.UNKNOWN_SSID

        val isRunning: Boolean
            get() = running.get()
    }
}