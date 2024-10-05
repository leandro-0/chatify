package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class UserModel {
    private String id;
    private String email;
    private String fcmToken;

    public UserModel(String id, String email, String fcmToken) {
        this.id = id;
        this.email = email;
        this.fcmToken = fcmToken;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public static UserModel fromDocument(DocumentSnapshot document) {
        return new UserModel(
                document.getId(),
                document.getString("email"),
                document.getString("fcmToken")
        );
    }
}
