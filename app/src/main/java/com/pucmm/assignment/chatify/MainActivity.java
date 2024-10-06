package com.pucmm.assignment.chatify;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pucmm.assignment.chatify.chats.ChatActivity;
import com.pucmm.assignment.chatify.core.models.ChatModel;
import com.pucmm.assignment.chatify.home.Home;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button signIn;
    TextView signUp;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterPage.class);
            startActivity(intent);
            finish();
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ||
                        TextUtils.isEmpty(password) || password.length() < 6) {
                    Toast.makeText(MainActivity.this, "Please fix the errors above", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String currentUserEmail = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
                                DatabaseReference userStatusRef = FirebaseDatabase.getInstance()
                                        .getReference("users").child(currentUserEmail.replace(".", ",")).child("status");

                                userStatusRef.setValue("online");

                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Snackbar.make(v, "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        /// Set focus change listeners to validate email and password fields
        editTextEmail.setOnFocusChangeListener(getEmailFocusChangeListener(editTextEmail));
        editTextPassword.setOnFocusChangeListener(getPasswordFocusChangeListener(editTextPassword));
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
        }

        if (getIntent().getExtras() != null && user != null) {
            String notificationChatId = getIntent().getExtras().getString("chatId");
            if (notificationChatId == null) return;

            db.collection("conversations").document(notificationChatId).get().addOnSuccessListener(documentSnapshot -> {
                final ChatModel chat = Home.transformDocumentToChat(
                        user.getEmail(),
                        documentSnapshot
                );

                Intent i = new Intent(this, ChatActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("chat", Parcels.wrap(chat));
                startActivity(i);
            });
        }
    }

    public static View.OnFocusChangeListener getEmailFocusChangeListener(EditText editTextEmail) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String email = String.valueOf(editTextEmail.getText());
                    if (TextUtils.isEmpty(email)) {
                        editTextEmail.setError("Email is required");
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        editTextEmail.setError("Invalid email format");
                    }
                }
            }
        };
    }

    public static View.OnFocusChangeListener getPasswordFocusChangeListener(EditText editTextPassword) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String password = String.valueOf(editTextPassword.getText());
                    if (TextUtils.isEmpty(password)) {
                        editTextPassword.setError("Password is required");
                    } else if (password.length() < 6) {
                        editTextPassword.setError("Password must be at least 6 characters");
                    }
                }
            }
        };
    }
}
