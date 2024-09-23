package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupChatModel extends ChatModel {
    private final Set<String> admins;
    private final String createdBy;

    public GroupChatModel(String title, LastMessageModel lastMessage, Timestamp createdAt, Set<String> members, Set<String> admins, String createdBy) {
        super(title, lastMessage, createdAt, members);
        this.admins = admins;
        this.createdBy = createdBy;
    }

    public Set<String> getAdmins() {
        return admins;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public static GroupChatModel fromDocument(String currentUserEmail, DocumentSnapshot document) {
        final Set<String> members = new HashSet<>((List<String>) document.get("members"));
        final Set<String> admins = new HashSet<>((List<String>) document.get("admins"));

        return new GroupChatModel(
                document.getString("name"),
                LastMessageModel.fromMap((Map<String, Object>) document.get("lastMessage")),
                document.get("createdAt", Timestamp.class),
                members,
                admins,
                document.getString("createdBy")
        );
    }
}
