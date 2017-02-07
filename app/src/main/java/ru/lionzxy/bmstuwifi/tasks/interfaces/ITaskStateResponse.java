package ru.lionzxy.bmstuwifi.tasks.interfaces;

/**
 * Created by lionzxy on 12.11.16.
 */

public interface ITaskStateResponse {
    public static int ERROR_STATE = -1;
    public static int INFINITE_STATES = -1; //In stateCount

    void onStateChange(String TAG, int stateDescribtionResId, int stateNumber, int stateCount);
}
