package ru.lionzxy.bmstuwifi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Activity with WIFI SSID supported list.
 *
 *
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project BMSTUWiFi
 * @date 27.11.16
 */
open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
