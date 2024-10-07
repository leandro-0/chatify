package com.pucmm.assignment.chatify.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.chats.ChatActivity;
import com.pucmm.assignment.chatify.core.models.ChatModel;
import com.pucmm.assignment.chatify.core.models.OneToOneChatModel;
import com.pucmm.assignment.chatify.core.utils.GeneralUtils;

import org.parceler.Parcels;

import java.util.Date;
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
        final ChatModel chat = chats.get(position);
        holder.nameView.setText(chat.getTitle());

        if (chat.getLastMessage() != null) {
            holder.lastMessageView.setText(chat.getLastMessage().getContent());

            final Date messageDate = chat.getLastMessage().getTimestamp().toDate();
            holder.timestampView.setText(GeneralUtils.isOlderThanADay(chat.getLastMessage().getTimestamp())
                    ? GeneralUtils.getFormattedDate(messageDate)
                    : GeneralUtils.getTimeIn24HourFormat(messageDate));

            // Highlight new chats
            if (chat.isNew()) {
                holder.timestampView.setTextColor(ContextCompat.getColor(context, R.color.indigo));
                holder.timestampView.setTypeface(holder.timestampView.getTypeface(), Typeface.BOLD);
            }
        } else {
            holder.lastMessageView.setText("No messages yet");
            holder.timestampView.setText("");
        }

        holder.imageView.setImageResource(chat instanceof OneToOneChatModel
                ? R.drawable.user
                : R.drawable.group);

        holder.itemView.setOnClickListener(v -> {
            chat.setNew(false);
            holder.timestampView.setTextColor(ContextCompat.getColor(context, R.color.light_gray));
            holder.timestampView.setTypeface(holder.timestampView.getTypeface(), Typeface.NORMAL);

            Intent i = new Intent(context.getApplicationContext(), ChatActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("chat", Parcels.wrap(chat));
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
