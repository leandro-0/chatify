package com.pucmm.assignment.chatify.search_people;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.assignment.chatify.R;

public class SearchPeopleViewHolder extends RecyclerView.ViewHolder {
    CheckBox checkBoxTile;

    public SearchPeopleViewHolder(@NonNull View itemView) {
        super(itemView);

        checkBoxTile = itemView.findViewById(R.id.personCheckBox);
    }
}
