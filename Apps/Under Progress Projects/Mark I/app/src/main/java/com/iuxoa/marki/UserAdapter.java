package com.iuxoa.marki;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    // Constructor
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    // ViewHolder class to hold the views for each user
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;
        Button editButton, deleteButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false); // Inflate the individual user item layout
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());

        // Set up edit button click listener
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditUserActivity.class);
            intent.putExtra("userName", user.getName());
            intent.putExtra("userEmail", user.getEmail());
            v.getContext().startActivity(intent);
        });

        // Set up delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            userList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
