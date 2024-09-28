package com.pucmm.assignment.chatify.core.utils;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class MessagesUtils {
    public static Map<String, Object> getMessageData(String currentUserEmail, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "text");
        data.put("content", message);
        data.put("sender", currentUserEmail);
        data.put("createdAt", Timestamp.now());

        return data;
    }

    public static Map<String, Object> getLastMessageData(Map<String, Object> messageData) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("content", messageData.get("content"));
        obj.put("timestamp", messageData.get("createdAt"));
        return obj;
    }
}
