package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class TextMessageModel extends  MessageModel {
    final private String content;

    public TextMessageModel(String id, String sender, Timestamp createdAt, String content) {
        super(id, sender, createdAt);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public static TextMessageModel fromDocument(DocumentSnapshot document) {
        return new TextMessageModel(
            document.getId(),
            document.getString("sender"),
            document.getTimestamp("createdAt"),
            document.getString("content")
        );
    }
}
