package com.pucmm.assignment.chatify.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.R;

public class ChatViewHolder  extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView, lastMessageView, timestampView;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.recentChatImageView);
        nameView = itemView.findViewById(R.id.recentChatName);
        lastMessageView = itemView.findViewById(R.id.recentChatLastMessage);
        imageView = itemView.findViewById(R.id.recentChatImageView);
        timestampView = itemView.findViewById(R.id.recentChatTimestamp);
    }
}
