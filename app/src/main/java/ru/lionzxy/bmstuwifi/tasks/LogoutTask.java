package ru.lionzxy.bmstuwifi.tasks;

import android.content.SharedPreferences;

import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;

/**
 * Created by lionzxy on 17.11.16.
 */

public class LogoutTask extends ITask {
    private String logout_id;

    public LogoutTask(String logout_id) {
        this.logout_id = logout_id;
    }

    @Override
    public boolean runTask() {
        //TODO Logout task
        return false;
    }
}
