package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.net.URI;

public class ImageMessageModel extends  MessageModel {
    final private URI imageUrl;
    
    public ImageMessageModel(String id, String sender, Timestamp createdAt, URI imageUrl) {
        super(id, sender, createdAt);
        this.imageUrl = imageUrl;
    }

    public URI getImageUrl() {
        return imageUrl;
    }

    public static ImageMessageModel fromDocument(DocumentSnapshot document) {
        return new ImageMessageModel(
            document.getId(),
            document.getString("sender"),
            document.getTimestamp("createdAt"),
            URI.create(document.getString("imageUrl"))
        );
    }
}
