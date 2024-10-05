package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.Timestamp;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Map;

@Parcel
public class LastMessageModel {
    private String content;
    private Timestamp timestamp;

    public LastMessageModel() {}

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
        if (data == null) return null;

        return new LastMessageModel(
                (String) data.get("content"),
                (Timestamp) data.get("timestamp")
        );
    }
}
