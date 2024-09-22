package com.pucmm.assignment.chatify.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.core.models.ChatModel;

import java.util.List;


public class RecentChatsAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private Context context;
    private List<ChatModel> chats;

    public RecentChatsAdapter(Context context, List<ChatModel> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(context).inflate(
                R.layout.recent_chat,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.nameView.setText(chats.get(position).getName());
        holder.lastMessageView.setText(chats.get(position).getLastMessage());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
