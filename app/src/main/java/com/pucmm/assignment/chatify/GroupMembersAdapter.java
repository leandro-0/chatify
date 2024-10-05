package com.pucmm.assignment.chatify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.core.models.UserModel;
import java.util.List;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {

    private final List<UserModel> members;

    public GroupMembersAdapter(List<UserModel> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel member = members.get(position);
        holder.memberName.setText(member.getName() != null ? member.getName() : member.getEmail());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberName;

        public ViewHolder(View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
        }
    }
}