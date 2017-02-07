package ru.lionzxy.bmstuwifi.authentificator;

import ru.lionzxy.bmstuwifi.interfaces.ITask;
import ru.lionzxy.bmstuwifi.tasks.AuthTask;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 07.02.17.
 */

public class BMSTUTeacherAuth extends IAuth {
    private ITask authTask = null;
    public static final String TEACHER_AUTH_SITE = "https://lbpfs.bmstu.ru:8003/index.php?zone=bmstu_stuff";

    public BMSTUTeacherAuth(Logger logger) {
        super(logger, "staff", "bmstu_staff");
    }

    @Override
    public void stop() {
        if (authTask != null)
            authTask.interrupt();
    }

    @Override
    public ITask registerInNetwork() {
        return new AuthTask(this);
    }

    @Override
    public String getAuthSite() {
        return TEACHER_AUTH_SITE;
    }

    @Override
    public String getLogoutSite() {
        return TEACHER_AUTH_SITE;
    }
}
