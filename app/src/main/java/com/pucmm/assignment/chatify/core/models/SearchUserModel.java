package com.pucmm.assignment.chatify.core.models;

import com.google.firebase.firestore.DocumentSnapshot;

import org.parceler.Parcel;

@Parcel
public class SearchUserModel {
    private String id;
    private String email;
    private boolean isSelected;

    public SearchUserModel() { }

    public SearchUserModel(String id, String email, boolean isSelected) {
        this.id = id;
        this.email = email;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public static SearchUserModel fromDocument(boolean isSelected, DocumentSnapshot document) {
        return new SearchUserModel(document.getId(), document.getString("email"), isSelected);
    }
}
