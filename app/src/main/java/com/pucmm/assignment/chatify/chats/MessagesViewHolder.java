package com.pucmm.assignment.chatify.chats;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.R;

public class MessagesViewHolder extends RecyclerView.ViewHolder {
    TextView senderNameView, messageContentView, messageTimestampView;

    public MessagesViewHolder(@NonNull View itemView) {
        super(itemView);

        senderNameView = itemView.findViewById(R.id.senderName);
        messageContentView = itemView.findViewById(R.id.messageContent);
        messageTimestampView = itemView.findViewById(R.id.messageTimestamp);
    }
}
