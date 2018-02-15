package ru.lionzxy.bmstuwifi.authentificator.provides

import android.content.Context
import okhttp3.FormBody
import okhttp3.Request
import org.jsoup.Jsoup
import ru.lionzxy.bmstuwifi.R
import ru.lionzxy.bmstuwifi.authentificator.NamedTask
import ru.lionzxy.bmstuwifi.authentificator.Provider
import java.util.*

class BMSTUStudentAuth(context: Context) : Provider(context) {
    companion object {
        const val STUDENT_AUTH_SITE = "https://lbpfs.bmstu.ru:8003/index.php?zone=bmstu_lb"
    }

    init {
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
                val doc = Jsoup.parse(html)
                val logout_id = doc.select("input[name=logout_id]").firstOrNull()?.attr("value")

                if (response.isSuccessful && !logout_id.isNullOrEmpty()) {
                    setLogoutId(logout_id!!)
                    vars["result"] = RESULT.CONNECTED
                } else {
                    vars["result"] = RESULT.NOT_REGISTERED
                }
                return response.isSuccessful
            }
        })
    }
}