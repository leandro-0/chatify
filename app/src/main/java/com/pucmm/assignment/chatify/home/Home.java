package com.pucmm.assignment.chatify.home;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.core.models.ChatModel;

import java.util.List;

public class Home extends AppCompatActivity {

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

        // Temp chats
        List<ChatModel> chats = List.of(
                new ChatModel("John Doe", "Hello"),
                new ChatModel("Jane Doe", "Hi"),
                new ChatModel("Alice", "Hey"),
                new ChatModel("Bob", "Hola")
        );

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRecentChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecentChatsAdapter(
                getApplicationContext(),
                chats
        ));
    }
}