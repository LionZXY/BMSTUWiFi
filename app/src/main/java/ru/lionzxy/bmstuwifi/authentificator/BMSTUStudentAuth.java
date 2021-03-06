package ru.lionzxy.bmstuwifi.authentificator;

import ru.lionzxy.bmstuwifi.tasks.AuthTask;
import ru.lionzxy.bmstuwifi.interfaces.ITask;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 05.11.16.
 */
public class BMSTUStudentAuth extends IAuth {
    private ITask authTask = null;
    public static final String STUDENT_AUTH_SITE = "https://lbpfs.bmstu.ru:8003/index.php?zone=bmstu_lb";

    public BMSTUStudentAuth(Logger logger) {
        super(logger, "lb", "bmstu_lb");
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
        return STUDENT_AUTH_SITE;
    }

    @Override
    public String getLogoutSite() {
        return STUDENT_AUTH_SITE;
    }
}
