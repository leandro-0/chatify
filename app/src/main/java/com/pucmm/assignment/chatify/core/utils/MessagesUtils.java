package com.pucmm.assignment.chatify.core.utils;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class MessagesUtils {

    // Método para obtener datos de un mensaje de texto
    public static Map<String, Object> getMessageData(String currentUserEmail, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "text");
        data.put("content", message);
        data.put("sender", currentUserEmail);
        data.put("createdAt", Timestamp.now());

        return data;
    }

    // Método para obtener datos de un mensaje de imagen
    public static Map<String, Object> getImageMessageData(String currentUserEmail, String imageUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "image");  // Indicamos que es un mensaje de tipo imagen
        data.put("imageUrl", imageUrl);  // Almacenamos la URL de la imagen
        data.put("sender", currentUserEmail);  // El remitente del mensaje
        data.put("createdAt", Timestamp.now());  // Marca de tiempo del mensaje

        return data;
    }

    // Método para obtener datos del último mensaje (puede ser texto o imagen)
    public static Map<String, Object> getLastMessageData(Map<String, Object> messageData) {
        Map<String, Object> obj = new HashMap<>();
        // Para el último mensaje mostramos el contenido si es texto o indicamos que es una imagen
        if (messageData.get("type").equals("text")) {
            obj.put("content", messageData.get("content"));
        } else {
            obj.put("content", "Image");
        }
        obj.put("timestamp", messageData.get("createdAt"));
        return obj;
    }
}
