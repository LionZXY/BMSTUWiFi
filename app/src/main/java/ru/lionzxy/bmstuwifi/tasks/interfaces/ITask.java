package ru.lionzxy.bmstuwifi.tasks.interfaces;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lionzxy on 12.11.16.
 */

public abstract class ITask {
    private List<WeakReference<ITaskStateResponse>> taskWeakResponses = new ArrayList<>();
    private boolean isInterrupt = false;

    public void interrupt() {
        isInterrupt = true;
    }

    public abstract boolean runTask();

    public boolean isInterrupt() {
        return isInterrupt;
    }

    public ITask subscribeOnStateChange(ITaskStateResponse taskStateResponse) {
        taskWeakResponses.add(new WeakReference<>(taskStateResponse));
        return this;
    }


    protected void onStateChange(int stateDescribtionResId, int stateNumber, int stateCount) {
        for (WeakReference<ITaskStateResponse> tskR : taskWeakResponses)
            if (tskR.get() != null)
                tskR.get().onStateChange(getTag(), stateDescribtionResId, stateNumber, stateCount);
    }

    protected void onStateChange(int stateDescribtionResId) {
        onStateChange(stateDescribtionResId, 0, 0);
    }

    public String getTag() {
        return "UNKNOWNTASK";
    }
}
