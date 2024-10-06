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

    public static Map<String, Object> getImageMessageData(String currentUserEmail, String imageUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "image");
        data.put("imageUrl", imageUrl);
        data.put("sender", currentUserEmail);
        data.put("createdAt", Timestamp.now());

        return data;
    }

    public static Map<String, Object> getLastMessageData(Map<String, Object> messageData) {
        Map<String, Object> obj = new HashMap<>();

        if (messageData.get("type").equals("text")) {
            String content = messageData.get("content").toString();
            obj.put("content", content.substring(0, Math.min(30, content.length())));
        } else {
            obj.put("content", "Image");
        }
        obj.put("timestamp", messageData.get("createdAt"));
        return obj;
    }
}
