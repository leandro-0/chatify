package com.pucmm.assignment.chatify.core.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserStatusUtils {
    static public void markUserStatus(
            UserStatus status,
            @NonNull com.google.android.gms.tasks.OnCompleteListener onCompleteListener
    ) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail == null) return;

        DatabaseReference userStatusRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userEmail.replace(".", ","))
                .child("status");

        userStatusRef.setValue(status.toString()).addOnCompleteListener(onCompleteListener);
    }
}
