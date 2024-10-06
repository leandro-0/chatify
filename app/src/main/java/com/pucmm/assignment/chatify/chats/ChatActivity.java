package com.pucmm.assignment.chatify.chats;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.core.models.ChatModel;
import com.pucmm.assignment.chatify.core.models.GroupChatModel;
import com.pucmm.assignment.chatify.core.models.ImageMessageModel;
import com.pucmm.assignment.chatify.core.models.MessageModel;
import com.pucmm.assignment.chatify.core.models.OneToOneChatModel;
import com.pucmm.assignment.chatify.core.models.TextMessageModel;
import com.pucmm.assignment.chatify.core.utils.GeneralUtils;
import com.pucmm.assignment.chatify.home.Home;
import com.pucmm.assignment.chatify.core.utils.MessagesUtils;
import com.pucmm.assignment.chatify.GroupDescriptionActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageReference;
    private List<MessageModel> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private ChatModel chat;
    private List<String> fmcTokens = new ArrayList<>();
    private String myFCMToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        ImageView uploadImageBtn = findViewById(R.id.uploadImageBtn);
        uploadImageBtn.setOnClickListener(v -> openFileChooser());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, Home.class);
            startActivity(intent);
            finish();
        });

        Intent currIntent = getIntent();
        chat = Parcels.unwrap(currIntent.getParcelableExtra("chat"));
        final TextView titleView = findViewById(R.id.chatName);
        final TextView chatStatus = findViewById(R.id.chatStatus);
        final ImageView sendBtn = findViewById(R.id.sendBtn);
        final EditText messageInput = findViewById(R.id.messageInput);

        if (chat instanceof OneToOneChatModel) {
            titleView.setText(((OneToOneChatModel) chat).getOtherMember(currentUserEmail));
            chatStatus.setVisibility(View.VISIBLE);
        } else if (chat instanceof GroupChatModel) {
            titleView.setText(((GroupChatModel) chat).getTitle());
            chatStatus.setVisibility(View.INVISIBLE);
            ImageView pfp = findViewById(R.id.userImage);
            pfp.setImageResource(R.drawable.group);
        }

        titleView.setOnClickListener(v -> {
            if (chat instanceof GroupChatModel) {
                Intent intent = new Intent(ChatActivity.this, GroupDescriptionActivity.class);
                intent.putExtra("groupChat", Parcels.wrap((GroupChatModel) chat));
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(getApplicationContext(), messages);
        recyclerView.setAdapter(adapter);

        sendBtn.setOnClickListener(v -> {
            final String message = messageInput.getText().toString();
            if (!message.isEmpty()) {
                sendMessage(message, false);
            }
        });

        db.collection("users").whereIn("email", new ArrayList(chat.getMembers()))
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String token = document.getString("fcmToken");
                        if (token == null || token.isEmpty()) continue;
                        if (currentUserEmail.equals(document.getString("email"))) {
                            myFCMToken = token;
                        } else {
                            fmcTokens.add(token);
                        }
                    }
                });

        db.collection("conversations").document(chat.getId()).collection("messages")
                .orderBy("createdAt")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    messages.clear();

                    value.getDocuments().stream()
                            .map(doc -> {
                                String type = doc.getString("type");
                                if (type.equalsIgnoreCase(MessageModel.imageTypeIdentifier)) {
                                    return ImageMessageModel.fromDocument(doc);
                                } else {
                                    return TextMessageModel.fromDocument(doc);
                                }
                            })
                            .forEach(messages::add);
                    adapter.notifyDataSetChanged();
                    if (!messages.isEmpty()) recyclerView.smoothScrollToPosition(messages.size() - 1);
                });

        if (chat instanceof OneToOneChatModel) {
            final TextView chatStatusView = findViewById(R.id.chatStatus);
            String otherUserEmail = ((OneToOneChatModel) chat).getOtherMember(currentUserEmail);
            DatabaseReference userStatusRef = FirebaseDatabase.getInstance()
                    .getReference("users").child(otherUserEmail.replace(".", ",")).child("status");

            userStatusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String status = snapshot.getValue(String.class);
                    if (status != null) {
                        chatStatus.setText(status.equals("online") ? "Online" : "Offline");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        sendMessage(imageUrl, true);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void sendMessage(String content, boolean isImage) {
        Map<String, Object> data;
        if (isImage) {
            // Sending an image message
            data = MessagesUtils.getImageMessageData(currentUserEmail, content);
        } else {
            // Sending a text message
            data = MessagesUtils.getMessageData(currentUserEmail, content);
        }

        db.collection("conversations").document(chat.getId()).collection("messages")
                .add(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recyclerView.smoothScrollToPosition(messages.size() - 1);
                        // Clear the EditText after sending the message
                        EditText messageInput = findViewById(R.id.messageInput);
                        messageInput.setText("");

                        sendNotifications(content, isImage);
                    } else {
                        Snackbar.make(findViewById(R.id.chatPage), "Failed to send the message", Snackbar.LENGTH_SHORT).show();
                    }
                });

        db.collection("conversations").document(chat.getId()).update(
                "lastMessage",
                MessagesUtils.getLastMessageData(data)
        );
    }

    private void sendNotifications(String message, boolean isImage) {
        if (fmcTokens.isEmpty()) return;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = GeneralUtils.getAccessToken();
                    OkHttpClient client = new OkHttpClient();
                    String title = chat instanceof OneToOneChatModel
                            ? currentUserEmail
                            : currentUserEmail + " | " + ((GroupChatModel) chat).getTitle();

                    for (final String fcmToken : fmcTokens) {
                        String json = "{\n" +
                                "  \"message\": {\n" +
                                "    \"token\": \"" + fcmToken + "\",\n" +
                                "    \"notification\": {\n" +
                                "      \"title\": \"" + title + "\",\n" +
                                (isImage
                                        ? "      \"image\": \"" + message + "\"\n"
                                        : "      \"body\": \"" + message + "\"\n") +
                                "    },\n" +
                                "    \"data\": {\n" +
                                "      \"chatId\": \"" + chat.getId() + "\"\n" +
                                "    }\n" +
                                "  }\n" +
                                "}";

                        RequestBody body = RequestBody.create(
                                json,
                                MediaType.parse("application/json; charset=utf-8")
                        );

                        client.newCall(new Request.Builder()
                                .url("https://fcm.googleapis.com/v1/projects/chatify-bb8e6/messages:send")
                                .header("Authorization", "Bearer " + token)
                                .addHeader("Content-Type", "application/json; UTF-8")
                                .post(body)
                                .build()).execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
