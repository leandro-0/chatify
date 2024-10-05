package com.pucmm.assignment.chatify.search_people;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucmm.assignment.chatify.MainActivity;
import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.chats.ChatActivity;
import com.pucmm.assignment.chatify.core.models.ChatModel;
import com.pucmm.assignment.chatify.core.models.GroupChatModel;
import com.pucmm.assignment.chatify.core.models.OneToOneChatModel;
import com.pucmm.assignment.chatify.core.models.SearchUserModel;
import com.pucmm.assignment.chatify.core.utils.MessagesUtils;
import com.pucmm.assignment.chatify.core.utils.ObservableSet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SearchPeople extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_people);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final List<SearchUserModel> users = new ArrayList<>();
        final ObservableSet<String> alreadySelectedUsers = new ObservableSet<>();
        final String currUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                .getEmail();

        RecyclerView recyclerView = findViewById(R.id.peopleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SeachPeopleAdapter adapter = new SeachPeopleAdapter(
                getApplicationContext(),
                users,
                ((checked, email) -> {
                    if (checked) {
                        alreadySelectedUsers.add(email);
                    } else {
                        alreadySelectedUsers.remove(email);
                    }
                })
        );
        recyclerView.setAdapter(adapter);

        final EditText queryEditText = findViewById(R.id.emailQuery);
        queryEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                final String query = editable.toString();

                db.collection("users")
                        .whereNotEqualTo("email", currUserEmail)
                        .whereGreaterThanOrEqualTo("email", query)
                        .whereLessThanOrEqualTo("email", query + "\uf8ff")
                        .limit(25)
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                users.clear();

                                task.getResult().getDocuments().stream()
                                        .map(document -> SearchUserModel.fromDocument(
                                                alreadySelectedUsers.contains(document.getString("email")),
                                                document
                                        ))
                                        .forEach(users::add);

                                adapter.notifyDataSetChanged();
                            }
                        });
            }
        });

        // Show users without any query
        queryEditText.setText("");

        alreadySelectedUsers.setChangeListener(new ObservableSet.SetChangeListener<String>() {
            @Override
            public void onAdd(String email) {
                showSelectedUsers(alreadySelectedUsers);
            }

            @Override
            public void onRemove(String email) {
                showSelectedUsers(alreadySelectedUsers);
            }
        });

        ImageView createChatButton = findViewById(R.id.createChat);
        createChatButton.setOnClickListener(v -> {
            if (alreadySelectedUsers.isEmpty()) {
                Toast.makeText(
                        SearchPeople.this,
                        "No users selected",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (alreadySelectedUsers.size() == 1) {
                String otherUserEmail = alreadySelectedUsers.iterator().next();

                db.collection("users")
                        .whereEqualTo("email", currUserEmail)
                        .get().addOnSuccessListener(task -> {
                            final DocumentSnapshot userDoc = task.getDocuments().get(0);
                            Map<String, Object> chats = (Map<String, Object>) userDoc.get("privateChats");

                            // Check if chat already exists
                            if (chats != null && chats.containsKey(otherUserEmail.replace('.', '#'))) {
                                String docId = (String) chats.get(otherUserEmail.replace('.', '#'));
                                db.collection("conversations").document(docId).get().addOnSuccessListener(
                                        documentSnapshot -> {
                                            final ChatModel chat = OneToOneChatModel.fromDocument(currUserEmail, documentSnapshot);

                                            Intent i = new Intent(SearchPeople.this, ChatActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            i.putExtra("chat", Parcels.wrap(chat));
                                            startActivity(i);
                                            finish();
                                        }
                                );
                                return;
                            }

                            Map<String, Object> data = new HashMap<>();
                            data.put("members", new ArrayList<>(List.of(currUserEmail, otherUserEmail)));
                            data.put("type", ChatModel.oneToOneIdentifier);
                            data.put("createdAt", Timestamp.now());
                            data.put("lastMessage", null);

                            db.collection("conversations").add(data).addOnSuccessListener(
                                    documentReference -> {
                                        final Task t1 = db.collection("users").
                                                document(userDoc.getId())
                                                .update(
                                                        "privateChats." + otherUserEmail.replace('.', '#'), documentReference.getId()
                                                );
                                        final Task t2 = db.collection("users")
                                                .whereEqualTo("email", otherUserEmail)
                                                .limit(1)
                                                .get().onSuccessTask(
                                                        task1 -> {
                                                            final DocumentSnapshot otherUserDoc = task1.getDocuments().get(0);
                                                            return db.collection("users")
                                                                    .document(otherUserDoc.getId())
                                                                    .update(
                                                                            "privateChats." + currUserEmail.replace('.', '#'), documentReference.getId()
                                                                    );
                                                        }
                                                );

                                        Tasks.whenAllSuccess(t1, t2).addOnSuccessListener(
                                                o -> {
                                                    final ChatModel chat = new OneToOneChatModel(
                                                            documentReference.getId(),
                                                            otherUserEmail,
                                                            null,
                                                            Timestamp.now(),
                                                            new HashSet<>(List.of(currUserEmail, otherUserEmail))
                                                    );

                                                    Intent i = new Intent(SearchPeople.this, ChatActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    i.putExtra("chat", Parcels.wrap(chat));
                                                    startActivity(i);
                                                    finish();
                                                }
                                        );
                                    });
                        });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter group name:");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                int padding = (int) (16 * getResources().getDisplayMetrics().density);
                layout.setPadding(padding, padding, padding, padding);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(input);
                builder.setView(layout);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    String groupName = input.getText().toString().trim();

                    if (!groupName.trim().isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("members", new ArrayList<>(alreadySelectedUsers));
                        ((List) data.get("members")).add(currUserEmail);
                        data.put("type", ChatModel.groupIdentifier);
                        data.put("admins", new ArrayList<>(List.of(currUserEmail)));
                        data.put("createdBy", currUserEmail);
                        data.put("name", groupName); // Usar el nombre del grupo ingresado
                        data.put("createdAt", Timestamp.now());
                        data.put("lastMessage", Map.of(
                                "content", "",
                                "timestamp", Timestamp.now()
                        ));

                        db.collection("conversations").add(data).addOnSuccessListener(
                                documentReference -> {
                                    final ChatModel chat = new GroupChatModel(
                                            documentReference.getId(),
                                            groupName,
                                            null,
                                            Timestamp.now(),
                                            new HashSet<>((List) data.get("members")),
                                            new HashSet<>(List.of(currUserEmail)),
                                            currUserEmail
                                    );

                                    Intent i = new Intent(SearchPeople.this, ChatActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("chat", Parcels.wrap(chat));
                                    startActivity(i);
                                    finish();
                                }
                        );
                    } else {
                        Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });

        ImageView closeButton = findViewById(R.id.closeBtn);
        closeButton.setOnClickListener(v -> {
            finish();
        });
    }

    void showSelectedUsers(ObservableSet<String> selectedUsers) {
        TextView textView = findViewById(R.id.selectedUsers);
        if (selectedUsers.isEmpty()) {
            textView.setText("No users selected");
            return;
        }
        textView.setText("Selected users: " + String.join(", ", selectedUsers));
    }
}