package ru.lionzxy.bmstuwifi


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.log_activity.*
import org.androidannotations.annotations.*
import ru.lionzxy.bmstuwifi.authentificator.AuthManager
import ru.lionzxy.bmstuwifi.interfaces.OnLogUpdate
import ru.lionzxy.bmstuwifi.services.ConnectionService
import ru.lionzxy.bmstuwifi.utils.AuthAsyncTaskLoader
import ru.lionzxy.bmstuwifi.utils.logs.Logger

/**
 * Created by lionzxy on 25.11.16.
 */

@EActivity(R.layout.log_activity)
class DebugActivity : AppCompatActivity(), OnLogUpdate, LoaderManager.LoaderCallbacks<Boolean> {
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

        logger.subscribeOnUpdate(this)
        updatelogoutIdText()
    }

    @Click(R.id.button_connect)
    fun onClickConnect() {
        val service = Intent(this, ConnectionService::class.java)
        startService(service)
        Toast.makeText(this, R.string.debug_retry_service, Toast.LENGTH_LONG).show()
        updatelogoutIdText()
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

    override fun onLogUpdate(level: Logger.Level, TAG: String, log: String) {
        runOnUiThread {
            if (this@DebugActivity.level == Logger.Level.DEBUG || this@DebugActivity.level == level)
                text_messages!!.append("[$TAG] $log\n")
            updatelogoutIdText()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle): android.support.v4.content.Loader<Boolean> {
        val asyncTaskLoader = AuthAsyncTaskLoader(this, progressDialog, null, args)
        asyncTaskLoader.forceLoad()
        return asyncTaskLoader
    }

    override fun onLoadFinished(loader: android.support.v4.content.Loader<Boolean>, data: Boolean?) {
        progressDialog!!.setMessage(getString(R.string.auth_finished))
    }

    override fun onLoaderReset(loader: Loader<Boolean>) {}

    private fun updatelogoutIdText() {
        var tmpLogout: String?
        val tmpBuilder = StringBuilder()
        for (ssid in AuthManager.getSSIDs()) {
            tmpLogout = AuthManager.getAuthForSSID(ssid).getLogoutId(null)
            if (tmpLogout != null)
                tmpBuilder.append(AuthManager.getAuthForSSID(ssid).nameid).append("_logout: ").append(tmpLogout).append('\n')
        }
        logoutIdText!!.text = tmpBuilder.toString()
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

}
