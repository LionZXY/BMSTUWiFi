package ru.lionzxy.bmstuwifi


import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.log_activity.*
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.CheckedChange
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EActivity
import ru.lionzxy.bmstuwifi.services.ConnectionService
import ru.lionzxy.bmstuwifi.utils.logs.Logger

/**
 * Created by lionzxy on 25.11.16.
 */

@EActivity(R.layout.log_activity)
class DebugActivity : AppCompatActivity() {
    private var level: Logger.Level = Logger.Level.INFO
    private val logger = Logger.getLogger()
    private val progressDialog: ProgressDialog? = null

    @AfterViews
    protected fun afterViews() {
        level = if (show_debug_log!!.isChecked) Logger.Level.DEBUG else Logger.Level.INFO

        val stringBuilder = StringBuilder()
        for (logString in logger.getLogByLevel(level))
            stringBuilder.append(logString).append("\n")
        text_messages!!.text = stringBuilder.toString()
    }

    @Click(R.id.button_connect)
    fun onClickConnect() {
        val service = Intent(this, ConnectionService::class.java)
        startService(service)
        Toast.makeText(this, R.string.debug_retry_service, Toast.LENGTH_LONG).show()
    }

    @CheckedChange(R.id.show_debug_log)
    internal fun checkedChangeOnshow_debug_log(show_debug_log: CompoundButton, isChecked: Boolean) {
        if (isChecked)
            level = Logger.Level.DEBUG
        else
            level = Logger.Level.INFO

        val stringBuilder = StringBuilder()
        for (logString in logger.getLogByLevel(level))
            stringBuilder.append(logString).append("\n")
        text_messages!!.text = stringBuilder.toString()
    }


}
