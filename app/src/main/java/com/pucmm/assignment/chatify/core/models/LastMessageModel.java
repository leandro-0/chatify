package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;

import java.util.Map;

public class LastMessageModel {
    private String content;
    private Timestamp timestamp;

    LastMessageModel(String content, Timestamp timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public static LastMessageModel fromMap(Map<String, Object> data) {
        return new LastMessageModel(
                (String) data.get("content"),
                (Timestamp) data.get("timestamp")
        );
    }
}
