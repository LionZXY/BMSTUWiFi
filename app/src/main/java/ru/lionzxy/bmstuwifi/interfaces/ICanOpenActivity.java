package ru.lionzxy.bmstuwifi.interfaces;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by lionzxy on 07.02.17.
 */

public interface ICanOpenActivity {
    void openActivity(Class<? extends Activity> activityClass, @Nullable Bundle extras,@Nullable String action);
}
