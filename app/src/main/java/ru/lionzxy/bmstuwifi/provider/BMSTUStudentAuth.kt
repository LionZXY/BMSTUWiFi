package ru.lionzxy.bmstuwifi.provider

import android.content.Context
import net.grandcentrix.tray.AppPreferences
import okhttp3.FormBody
import okhttp3.Request
import ru.lionzxy.wifijob.R
import ru.lionzxy.wifijob.authentificator.NamedTask
import ru.lionzxy.wifijob.authentificator.Provider
import ru.lionzxy.wifijob.utils.Listener
import java.util.*
import java.util.regex.Pattern

class BMSTUStudentAuth(context: Context) : Provider(context) {
    companion object {
        const val STUDENT_AUTH_SITE = "https://lbpfs.bmstu.ru:8003/index.php?zone=bmstu_lb"
        val REG_EXPLOGOUT = Pattern.compile("\'<input name=\"logout_id\" type=\"hidden\" value=([\"][\\w]+\")")

    }

    private val sharedPreferences: AppPreferences by lazy {
        AppPreferences(context)
    }

    init {
        running.subscribe(object : Listener<Boolean>(true) {
            override fun onChange(new_value: Boolean) {
                if (!new_value) {
                    setLogoutId("")
                }
            }
        })

        add(object : NamedTask(context.getString(R.string.notification_connection_check)) {
            override fun run(vars: HashMap<String, Any>): Boolean {
                if (isConnected) {
                    vars["result"] = RESULT.ALREADY_CONNECTED
                    return false
                }
                return true
            }
        })

        add(object : NamedTask(context.getString(R.string.notification_register_check)) {
            override fun run(vars: HashMap<String, Any>): Boolean {
                setLogin("knyu16u287")
                setPassword("5ka3kn7w")

                if (getLogin("") == "" || getPassword("") == "") {
                    vars["result"] = RESULT.NOT_REGISTERED
                    // TODO
                    return true
                }
                return true
            }
        })

        add(object : NamedTask(context.getString(R.string.notification_register)) {
            override fun run(vars: HashMap<String, Any>): Boolean {
                val formBody = FormBody.Builder()
                        .add("redirurl", "/")
                        .add("auth_user", getLogin(""))
                        .add("auth_pass", getPassword(""))
                        .add("accept", "Continue")
                        .build()
                val request = Request.Builder()
                        .url(STUDENT_AUTH_SITE)
                        .post(formBody)
                        .build()

                val response = client.newCall(request).execute()

                val html = response.body()!!.string()
                val regexResult = REG_EXPLOGOUT.matcher(html)

                if (regexResult.find() && response.isSuccessful) {
                    val logoutId = regexResult.group(1)
                    setLogoutId(logoutId.substring(1, logoutId.length - 2))
                    vars["result"] = RESULT.CONNECTED
                } else {
                    vars["result"] = RESULT.NOT_REGISTERED
                }
                return response.isSuccessful
            }
        })
    }

    fun getLogin(defaultV: String): String {

        return sharedPreferences.getString(getNameid() + "_login", defaultV) ?: defaultV
    }

    fun getPassword(defaultV: String): String {
        return sharedPreferences.getString(getNameid() + "_password", defaultV) ?: defaultV
    }

    fun getLogoutId(defaultV: String): String {
        return sharedPreferences.getString(getNameid() + "_logout", defaultV) ?: defaultV
    }

    fun setLogin(login: String): Provider {
        sharedPreferences.put(getNameid() + "_login", login)
        return this
    }

    fun setPassword(password: String): Provider {
        sharedPreferences.put(getNameid() + "_password", password)
        return this
    }

    fun setLogoutId(logoutId: String): Provider {
        sharedPreferences.put(getNameid() + "_logout", logoutId)
        return this
    }

    private fun getNameid(): String {
        return name
    }
}