package ru.lionzxy.bmstuwifi.tasks.interfaces;

/**
 * Created by lionzxy on 12.11.16.
 */

public abstract class ITask {
    private ITaskStateResponse taskStateResponse;
    private boolean isInterrupt = false;


    public void interrupt() {
        isInterrupt = true;
    }

    public abstract boolean runTask();

    public boolean isInterrupt() {
        return isInterrupt;
    }

    public ITask subscribeOnStateChange(ITaskStateResponse taskStateResponse) {
        this.taskStateResponse = taskStateResponse;
        return this;
    }


    protected void onStateChange(int stateDescribtionResId, int stateNumber, int stateCount) {
        if (taskStateResponse != null)
            taskStateResponse.onStateChange(stateDescribtionResId, stateNumber, stateCount);
    }

}
