package com.pucmm.assignment.chatify.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotificationServices extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        if (!remoteMessage.getData().isEmpty()) {
            System.out.println("Message data payload: " + remoteMessage.getData());
        }
    }
}
