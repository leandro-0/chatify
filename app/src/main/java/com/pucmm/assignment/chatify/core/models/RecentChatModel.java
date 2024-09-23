package com.pucmm.assignment.chatify.core.models;


import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class RecentChatModel {
    private final String name;
    private final String lastMessage;

    public RecentChatModel(String name, String lastMessage) {
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public static RecentChatModel fromDocument(String currentUserEmail, DocumentSnapshot document) {
        final List<String> members = (List<String>) document.get("members");
        assert members != null;

        return new RecentChatModel(
                members.stream().filter(member -> !member.equals(currentUserEmail))
                        .findFirst().orElse("Unknown"),
                document.getString("lastMessage")
        );
    }
}
