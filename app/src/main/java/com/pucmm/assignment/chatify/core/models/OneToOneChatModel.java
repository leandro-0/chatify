package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OneToOneChatModel extends ChatModel {
    public OneToOneChatModel(String id, String title, LastMessageModel lastMessage, Timestamp createdAt, Set<String> members) {
        super(id, title, lastMessage, createdAt, members);
    }

    public static OneToOneChatModel fromDocument(String currentUserEmail, DocumentSnapshot document) {
        final Set<String> members = new HashSet<>((List<String>) document.get("members"));

        return new OneToOneChatModel(
                document.getId(),
                members.stream().filter(member -> !member.equals(currentUserEmail))
                        .findFirst().orElse("Unknown"),
                LastMessageModel.fromMap((Map<String, Object>) document.get("lastMessage")),
                document.get("createdAt", Timestamp.class),
                members
        );
    }
}
