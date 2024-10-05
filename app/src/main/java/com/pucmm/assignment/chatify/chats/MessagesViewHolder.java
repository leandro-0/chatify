package com.pucmm.assignment.chatify.chats;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.R;

public class MessagesViewHolder extends RecyclerView.ViewHolder {
    TextView senderNameView, messageContentView, messageTimestampView;
    ImageView messageImageView;
    CardView messageCardView;

    public MessagesViewHolder(@NonNull View itemView) {
        super(itemView);

        senderNameView = itemView.findViewById(R.id.senderName);
        messageContentView = itemView.findViewById(R.id.messageContent);
        messageTimestampView = itemView.findViewById(R.id.messageTimestamp);
        messageCardView = itemView.findViewById(R.id.messageImageCard);
        messageImageView = itemView.findViewById(R.id.messageImage);
    }
}