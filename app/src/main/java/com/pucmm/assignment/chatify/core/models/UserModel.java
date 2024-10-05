package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class UserModel {
    private String id;
    private String name;
    private String email;
    private String fcmToken;

    public UserModel() {}

    public UserModel(String id, String name, String email, String fcmToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.fcmToken = fcmToken;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static UserModel fromDocument(DocumentSnapshot document) {
        return new UserModel(
                document.getId(),
                document.getString("name"),
                document.getString("email"),
                document.getString("fcmToken")
        );
    }
}
