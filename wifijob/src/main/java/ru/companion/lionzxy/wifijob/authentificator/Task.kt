/**
 * This class had been forked and it was changed for own tasks from repository "Wi-Fi в метро" (pw.thedrhax.mosmetro, Moscow Wi-Fi autologin)
 * Copyright © 2015 Dmitry Karikh <the.dr.hax@gmail.com>
 */

package ru.companion.lionzxy.wifijob.authentificator

import java.util.*

interface Task {
    /**
     * Main body of the Task.
     * @param vars  Mutable Map for sending and receiving data between Tasks.
     * @return      True on success; false on unrecoverable exception.
     */
    fun run(vars: HashMap<String, Any>): Boolean

}