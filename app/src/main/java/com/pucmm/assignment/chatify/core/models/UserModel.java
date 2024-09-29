package com.pucmm.assignment.chatify.core.models;

public class UserModel {
    private String email;
    private String name;
    // Add other fields as necessary

    // Default constructor required for Firestore
    public UserModel() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}