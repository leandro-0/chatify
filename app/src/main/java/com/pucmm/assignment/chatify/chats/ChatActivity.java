package com.pucmm.assignment.chatify.chats;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.core.models.ChatModel;
import com.pucmm.assignment.chatify.core.models.GroupChatModel;
import com.pucmm.assignment.chatify.core.models.ImageMessageModel;
import com.pucmm.assignment.chatify.core.models.MessageModel;
import com.pucmm.assignment.chatify.core.models.OneToOneChatModel;
import com.pucmm.assignment.chatify.core.models.TextMessageModel;
import com.pucmm.assignment.chatify.home.RecentChatsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent currIntent = getIntent();
        String chatId = currIntent.getStringExtra("chatId");

        final List<MessageModel> messages = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MessagesAdapter adapter = new MessagesAdapter(
                getApplicationContext(),
                messages
        );
        recyclerView.setAdapter(adapter);

        db.collection("conversations").document(chatId).collection("messages")
                .orderBy("createdAt")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    messages.clear();

                    value.getDocuments().stream()
                            .map(doc -> {
                                String type = doc.getString("type");
                                assert type != null;

                                if (type.equalsIgnoreCase(MessageModel.imageTypeIdentifier)) {
                                    return ImageMessageModel.fromDocument(doc);
                                } else {
                                    return TextMessageModel.fromDocument(doc);
                                }
                            })
                            .forEach(messages::add);
                    adapter.notifyDataSetChanged();
                });
    }

}