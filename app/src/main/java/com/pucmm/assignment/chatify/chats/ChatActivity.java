package com.pucmm.assignment.chatify.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.core.models.ChatModel;
import com.pucmm.assignment.chatify.core.models.GroupChatModel;
import com.pucmm.assignment.chatify.core.models.ImageMessageModel;
import com.pucmm.assignment.chatify.core.models.MessageModel;
import com.pucmm.assignment.chatify.core.models.OneToOneChatModel;
import com.pucmm.assignment.chatify.core.models.TextMessageModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

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
        ChatModel chat = Parcels.unwrap(currIntent.getParcelableExtra("chat"));
        final TextView titleView = (TextView) findViewById(R.id.chatName);
        // TODO: Implement online/offline status
        final TextView chatStatus = (TextView) findViewById(R.id.chatStatus);

        if (chat instanceof OneToOneChatModel) {
            titleView.setText(((OneToOneChatModel) chat).getOtherMember(currentUserEmail));
        } else {
            chatStatus.setVisibility(View.INVISIBLE);
            titleView.setText(((GroupChatModel) chat).getTitle());
        }

        final List<MessageModel> messages = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MessagesAdapter adapter = new MessagesAdapter(
                getApplicationContext(),
                messages
        );
        recyclerView.setAdapter(adapter);

        db.collection("conversations").document(chat.getId()).collection("messages")
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