package com.bachmann.nfcsound;

import android.app.ActivityManager;
import android.os.Build;

public class AppStatus {

    private ActivityManager activity_manager;

    public AppStatus(ActivityManager act_mngr) {
        activity_manager =act_mngr;
    }

    public boolean isAppLocked () {
        return isTaskLocked();
    }

    private boolean isTaskLocked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For SDK version 23 and above.
            return activity_manager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_PINNED;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK version >= 21. This API is deprecated in 23.
            return activity_manager.isInLockTaskMode();
        }

        return false;
    }
}
