package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import org.parceler.Parcel;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Parcel
public class GroupChatModel extends ChatModel {
    private Set<String> admins;
    private String createdBy;
    private String imageUrl;

    public GroupChatModel() {}

    public GroupChatModel(String id, String title, LastMessageModel lastMessage, Timestamp createdAt, Set<String> members, Set<String> admins, String createdBy, String imageUrl) {
        super(id, title, lastMessage, createdAt, members);
        this.admins = admins;
        this.createdBy = createdBy;
        this.imageUrl = imageUrl;
    }

    public Set<String> getAdmins() {
        return admins;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static GroupChatModel fromDocument(String currentUserEmail, DocumentSnapshot document) {
        final Set<String> members = new HashSet<>((List<String>) document.get("members"));
        final Set<String> admins = new HashSet<>((List<String>) document.get("admins"));

        return new GroupChatModel(
                document.getId(),
                document.getString("name"),
                LastMessageModel.fromMap((Map<String, Object>) document.get("lastMessage")),
                document.get("createdAt", Timestamp.class),
                members,
                admins,
                document.getString("createdBy"),
                document.getString("imageUrl")
        );
    }
}