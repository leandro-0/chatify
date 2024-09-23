package com.pucmm.assignment.chatify.home;

import android.os.Bundle;

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
import com.pucmm.assignment.chatify.core.models.OneToOneChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Home extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final List<ChatModel> chats = new ArrayList<>();
        // TODO: Session management
        final String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                .getEmail();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRecentChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecentChatsAdapter adapter = new RecentChatsAdapter(
                getApplicationContext(),
                chats
        );
        recyclerView.setAdapter(adapter);

        db.collection("conversations")
            .whereArrayContains("members", userEmail)
            .addSnapshotListener((value, error) -> {
                if (error != null || value == null) return;

                chats.clear();

                value.getDocuments().stream()
                        .map(doc -> {
                            String type = doc.getString("type");
                            assert type != null;

                            if (type.equalsIgnoreCase(ChatModel.groupIdentifier)) {
                                return GroupChatModel.fromDocument(userEmail, doc);
                            } else {
                                return OneToOneChatModel.fromDocument(userEmail, doc);
                            }
                        })
                        .forEach(chats::add);

                adapter.notifyDataSetChanged();
            });
    }
}