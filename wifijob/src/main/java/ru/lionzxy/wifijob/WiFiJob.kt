@file:JvmName("WiFiJob")

package ru.lionzxy.wifijob

import ru.lionzxy.wifijob.authentificator.Provider

fun addProvider(ssid: String, providerClazz: Class<out Provider>) {
    Provider.addSSID(ssid, providerClazz)
}