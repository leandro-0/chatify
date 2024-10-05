package com.pucmm.assignment.chatify.search_people;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.R;
import com.pucmm.assignment.chatify.core.models.SearchUserModel;

import java.util.List;

public class SeachPeopleAdapter extends RecyclerView.Adapter<SearchPeopleViewHolder>{
    private Context context;
    private List<SearchUserModel> users;
    private SelectedUserAction selectedUserAction;

    public SeachPeopleAdapter(Context context, List<SearchUserModel> users, SelectedUserAction selectedUserAction) {
        this.context = context;
        this.users = users;
        this.selectedUserAction = selectedUserAction;
    }

    @NonNull
    @Override
    public SearchPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchPeopleViewHolder(LayoutInflater.from(context).inflate(
                R.layout.create_chat_tile,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPeopleViewHolder holder, int position) {
        final SearchUserModel user = users.get(position);
        holder.checkBoxTile.setText(user.getEmail());
        holder.checkBoxTile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedUserAction.mark(isChecked, user.getEmail());
        });
        holder.checkBoxTile.setChecked(user.isSelected());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
