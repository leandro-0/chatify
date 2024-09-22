package com.pucmm.assignment.chatify.core.models;


public class ChatModel {
    private final String name;
    private final String lastMessage;

    public ChatModel(String name, String lastMessage) {
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
