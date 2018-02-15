/**
 * This class had been forked and it was changed for own tasks from repository "Wi-Fi в метро" (pw.thedrhax.mosmetro, Moscow Wi-Fi autologin)
 * Copyright © 2015 Dmitry Karikh <the.dr.hax@gmail.com>
 */

package ru.lionzxy.bmstuwifi.authentificator

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import ru.lionzxy.bmstuwifi.App
import ru.lionzxy.bmstuwifi.authentificator.provides.BMSTUStudentAuth
import ru.lionzxy.bmstuwifi.utils.Listener
import ru.lionzxy.bmstuwifi.utils.Randomizer
import ru.lionzxy.bmstuwifi.utils.Util
import ru.lionzxy.bmstuwifi.utils.logs.Logger
import java.io.IOException
import java.util.*

/**
 * Base class for all providers.
 *
 * @author Dmitry Karikh <the.dr.hax></the.dr.hax>@gmail.com>
 * @see LinkedList
 *
 * @see Task
 */

abstract class Provider(protected var context: Context) : LinkedList<Task>() {
    protected var settings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    protected var random: Randomizer = Randomizer(context)
    protected val client: OkHttpClient

    /**
     * Number of retries for each request
     */
    private var pref_retry_count: Int = 0

    /**
     * Checks network connection state for a specific provider.
     *
     * @return True if internet access is available; otherwise, false is returned.
     */
    val isConnected: Boolean
        get() = generate_204(context, running).code() == 204

    /**
     * Get Provider's short description.
     *
     * @return Provider's name.
     */
    val name: String
        get() = this.javaClass.simpleName

    /**
     * Listener used to stop Provider immediately after
     * variable is changed by another thread
     */
    private val running = Listener(true)

    /**
     * Method used to check if Provider must finish as soon as possible.
     *
     * @return true is Provider must stop, otherwise false.
     */
    private val isStopped: Boolean
        get() = !running.get()

    private var callback: ICallback = object : ICallback {
        override fun onProgressUpdate(progress: Int) {

        }

        override fun onProgressUpdate(progress: Int, message: String) {

        }
    }

    init {
        this.pref_retry_count = Util.getIntPreference(context, "pref_retry_count", 3)
        this.client = OkHttpClient()
    }

    enum class RESULT {
        CONNECTED, ALREADY_CONNECTED, // Success
        NOT_REGISTERED, ERROR, NOT_SUPPORTED, // Error
        INTERRUPTED                             // Stopped
    }

    /**
     * Start the connection sequence defined in child classes.
     */
    fun start(): RESULT {
        val vars = HashMap<String, Any>()
        vars["result"] = RESULT.ERROR

        var progress: Int
        for (task in this) {
            if (isStopped) return RESULT.INTERRUPTED
            progress = (indexOf(task) + 1) * 100 / size
            if (task is NamedTask) {
                Logger.log(task.name)
                callback.onProgressUpdate(progress, task.name)
            } else {
                callback.onProgressUpdate(progress)
            }
            if (!task.run(vars)) break
        }

        return vars["result"] as RESULT
    }

    /**
     * Subscribe to another Listener to implement cascade notifications
     */
    fun setRunningListener(master: Listener<Boolean>): Provider {
        running.subscribe(master)
        return this
    }

    /**
     * The ICallback interface is used by other classes to get messages from Provider
     * during runtime.
     */
    interface ICallback {
        /**
         * Report the progress of algorithm execution.
         *
         * @param progress Any Integer between 0 and 100.
         */
        fun onProgressUpdate(progress: Int)

        /**
         * Report the progress of algorithm execution with the description of current Task.
         *
         * @param progress Any Integer between 0 and 100.
         * @param message  Text massage to display in notification.
         */
        fun onProgressUpdate(progress: Int, message: String)
    }

    /**
     * Set callback for this Provider.
     *
     * @param callback Any implementation of the ICallback interface.
     * @return Saved instance of this Provider.
     */
    fun setCallback(callback: ICallback): Provider {
        this.callback = callback
        return this
    }

    fun getLogin(defaultV: String): String {

        return App.get().sharedPreferences.getString(getNameid() + "_login", defaultV)
    }

    fun getPassword(defaultV: String): String {
        return App.get().sharedPreferences.getString(getNameid() + "_password", defaultV)
    }

    fun getLogoutId(defaultV: String): String {
        return App.get().sharedPreferences.getString(getNameid() + "_logout", defaultV)
    }

    fun setLogin(login: String): Provider {
        App.get().sharedPreferences.edit().putString(getNameid() + "_login", login).apply()
        return this
    }

    fun setPassword(password: String): Provider {
        App.get().sharedPreferences.edit().putString(getNameid() + "_password", password).apply()
        return this
    }

    fun setLogoutId(logoutId: String): Provider {
        App.get().sharedPreferences.edit().putString(getNameid() + "_logout", logoutId).apply()
        return this
    }

    fun getNameid(): String {
        return name
    }

    companion object {
        /**
         * URL used to detect if Captive Portal is present in the current network.
         */
        protected val GENERATE_204 = arrayOf("http://www.google.ru/generate_204",
                "http://www.google.ru/gen_204",
                "http://www.google.com/generate_204",
                "http://www.google.com/gen_204",
                "http://connectivitycheck.gstatic.com/generate_204",
                "http://www.gstatic.com/generate_204")

        /**
         * List of supported SSIDs
         */
        val SSIDs = arrayOf("bmstu_lb", "bmstu_staff", "bmstu_guest")

        /**
         * Find Provider using already received response from server.
         *
         * @param context Android Context required to create the new instance.
         * @param ssid    SSID wifi network
         * @return New Provider instance.
         * @see IAuth
         */
        fun find(context: Context, ssid: String): Provider {
            return when (ssid) {
                SSIDs[0] -> BMSTUStudentAuth(context)
                else -> object : Provider(context) {}
            }
        }

        /**
         * Check if a particular SSID is supported.
         *
         * @param SSID SSID of the Wi-Fi network to be tested.
         * @return True if network is supported; otherwise, false.
         */
        fun isSSIDSupported(SSID: String): Boolean {
            for (a in SSIDs) {
                if (a == SSID)
                    return true
            }
            return false
        }

        /**
         * Checks network connection state without binding to a specific provider.
         * This implementation uses generate_204 method, that is default for Android.
         *
         * @return ParsedResponse that contains response code to be compared with 204.
         */
        fun generate_204(context: Context, running: Listener<Boolean>): Response {
            val random = Randomizer(context)

            val request = Request.Builder().url(random.choose(GENERATE_204) as String).build()

            var response = Response.Builder().request(request)
                    .protocol(Protocol.HTTP_2)
                    .message("")
                    .code(404).build()
            try {
                response = OkHttpClient().newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (response.code() != 204) return response

            try {
                response = OkHttpClient().newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return response
        }
    }
}
