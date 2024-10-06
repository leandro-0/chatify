package com.pucmm.assignment.chatify;

import android.app.Application;
import android.content.ComponentCallbacks2;

import com.google.firebase.auth.FirebaseAuth;
import com.pucmm.assignment.chatify.core.utils.UserStatus;
import com.pucmm.assignment.chatify.core.utils.UserStatusUtils;
import com.pucmm.assignment.chatify.home.Home;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        // Mark as offline when the app is hidden
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            UserStatusUtils.markUserStatus(UserStatus.OFFLINE, task -> {});
        }
    }
}
