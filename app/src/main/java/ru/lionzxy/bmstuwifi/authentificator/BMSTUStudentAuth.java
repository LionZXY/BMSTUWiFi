package ru.lionzxy.bmstuwifi.authentificator;

import java.io.IOException;

import ru.lionzxy.bmstuwifi.tasks.interfaces.ITask;
import ru.lionzxy.bmstuwifi.utils.Logger;

/**
 * Created by lionzxy on 05.11.16.
 */
public class BMSTUStudentAuth extends IAuth {
    private ITask authTask = null;

    public BMSTUStudentAuth(Logger logger) {
        super(logger, "bmstu_lb");
    }

    @Override
    public void stop() {
        if(authTask != null)
            authTask.interrupt();
    }

    @Override
    public ITask registerInNetwork() throws IOException {
        //TODO
        return null;
    }
}
