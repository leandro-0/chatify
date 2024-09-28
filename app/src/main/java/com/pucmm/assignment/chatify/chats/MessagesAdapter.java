package com.pucmm.assignment.chatify.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.core.models.ImageMessageModel;
import com.pucmm.assignment.chatify.core.models.MessageModel;
import com.pucmm.assignment.chatify.core.models.TextMessageModel;
import com.pucmm.assignment.chatify.core.utils.GeneralUtils;

import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private Context context;
    private List<MessageModel> messages;
    private String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    public MessagesAdapter(Context context, List<MessageModel> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessagesViewHolder(LayoutInflater.from(context).inflate(
                viewType == R.layout.sender_chat_bubble
                        ? R.layout.sender_chat_bubble
                        : R.layout.receiver_chat_bubble,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        final MessageModel message = messages.get(position);

        holder.senderNameView.setText(message.getSender());
        if (message instanceof TextMessageModel) {
            holder.messageContentView.setText(((TextMessageModel) message).getContent());
            holder.messageContentView.setVisibility(View.VISIBLE);
            holder.messageImageView.setVisibility(View.GONE);
        } else if (message instanceof ImageMessageModel) {
            String imageUrl = String.valueOf(((ImageMessageModel) message).getImageUrl());
            Glide.with(context).load(imageUrl).into(holder.messageImageView);
            holder.messageImageView.setVisibility(View.VISIBLE);
            holder.messageContentView.setVisibility(View.GONE);
        }

        final Date messageDate = message.getCreatedAt().toDate();
        holder.messageTimestampView.setText(GeneralUtils.isOlderThanADay(message.getCreatedAt())
                ? GeneralUtils.getFullFormattedDate(messageDate)
                : GeneralUtils.getTimeIn24HourFormat(messageDate));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        // TODO: implement image message type
        return messages.get(position).getSender().equalsIgnoreCase(currentUserEmail)
                ? R.layout.sender_chat_bubble
                : R.layout.receiver_chat_bubble;
    }
}
