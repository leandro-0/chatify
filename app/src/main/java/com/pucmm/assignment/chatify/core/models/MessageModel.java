package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public abstract class MessageModel {
    public static String imageTypeIdentifier = "image";

    final private String id;
    final private String sender;
    final private Timestamp createdAt;

    public MessageModel(String id, String sender, Timestamp createdAt) {
        this.id = id;
        this.sender = sender;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public static MessageModel fromDocument(DocumentSnapshot document) {
        return null;
    }
}
