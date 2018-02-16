@file:JvmName("WiFiJob")

package ru.companion.lionzxy.wifijob

import ru.companion.lionzxy.wifijob.authentificator.Provider

fun addProvider(ssid: String, providerClazz: Class<out Provider>) {
    Provider.addSSID(ssid, providerClazz)
}