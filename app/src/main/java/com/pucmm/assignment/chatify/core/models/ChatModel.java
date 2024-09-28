package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Set;

public abstract class ChatModel {
    public static String groupIdentifier = "group";

    private String id;
    private String title;
    private LastMessageModel lastMessage;
    private Timestamp createdAt;
    private Set<String> members;

    public ChatModel() {}

    ChatModel(String id, String title, LastMessageModel lastMessage, Timestamp createdAt, Set<String> members) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.createdAt = createdAt;
        this.members = members;
    }

    public String getTitle() {
        return title;
    }

    public LastMessageModel getLastMessage() {
        return lastMessage;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Set<String> getMembers() {
        return members;
    }

    public String getId() {
        return id;
    }

    public static ChatModel fromDocument(String currentUserEmail, DocumentSnapshot document) {
        return null;
    }
}
