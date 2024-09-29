package com.pucmm.assignment.chatify;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pucmm.assignment.chatify.core.models.GroupChatModel;
import com.pucmm.assignment.chatify.core.models.UserModel;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GroupDescriptionActivity extends AppCompatActivity {

    private static final String TAG = "GroupDescriptionActivity";
    private ImageView groupImage;
    private TextView groupName, groupCreatedAt;
    private RecyclerView groupMembersRecyclerView;
    private GroupMembersAdapter adapter;
    private List<UserModel> members = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_description);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        groupImage = findViewById(R.id.groupImage);
        groupName = findViewById(R.id.groupName);
        groupCreatedAt = findViewById(R.id.groupCreatedAt);
        groupMembersRecyclerView = findViewById(R.id.groupMembersRecyclerView);

        groupMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupMembersAdapter(members);
        groupMembersRecyclerView.setAdapter(adapter);

        GroupChatModel groupChat = Parcels.unwrap(getIntent().getParcelableExtra("groupChat"));

        if (groupChat != null) {
            groupName.setText(groupChat.getTitle());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String formattedDate = sdf.format(groupChat.getCreatedAt().toDate());
            groupCreatedAt.setText("Created on: " + formattedDate);

            if (groupChat.getImageUrl() != null && !groupChat.getImageUrl().isEmpty()) {
                Glide.with(this).load(groupChat.getImageUrl()).into(groupImage);
            } else {
                groupImage.setImageResource(R.drawable.group);
            }
            loadGroupMembers(groupChat);
        } else {
            Toast.makeText(this, "Failed to load group information", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadGroupMembers(GroupChatModel groupChat) {
        List<String> memberEmails = new ArrayList<>(groupChat.getMembers());
        Log.d(TAG, "Loading members: " + memberEmails);

        db.collection("users")
                .whereIn("email", memberEmails)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        members.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserModel user = document.toObject(UserModel.class);
                            members.add(user);
                            Log.d(TAG, "Added member - Email: " + user.getEmail() + ", Name: " + user.getName());
                            Log.d(TAG, "Raw document data: " + document.getData());
                        }
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Members loaded: " + members.size());
                    } else {
                        Log.e(TAG, "Failed to load group members", task.getException());
                        Toast.makeText(this, "Failed to load group members", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}