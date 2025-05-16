package com.iuxoa.datadrop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> notifications;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;

    public NotificationAdapter(Context context, List<NotificationModel> notifications, String userId) {
        this.context = context;
        this.notifications = notifications;
        this.userId = userId;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());

        // Dim read notifications
        if (notification.isRead()) {
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!notification.isRead()) {
                db.collection("users").document(userId)
                        .collection("notifications").document(notification.getId())
                        .update("isRead", true)
                        .addOnSuccessListener(aVoid -> {
                            notification.setRead(true);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Marked as read", Toast.LENGTH_SHORT).show();
                            // TODO: Add action to open notification details if needed
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to mark as read", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Already read - handle as needed
                Toast.makeText(context, "Notification already read", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
