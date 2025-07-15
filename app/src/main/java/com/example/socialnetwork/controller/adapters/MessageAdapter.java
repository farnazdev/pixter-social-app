package com.example.socialnetwork.controller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Message;

import java.util.List;
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_layout_of_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            // پیام خودت (فرستنده)
            holder.senderLayout.setVisibility(View.VISIBLE);
            holder.receiverLayout.setVisibility(View.GONE);
            holder.senderMessageText.setText(message.getMessageText());
            holder.senderTime.setText(message.getTime());
        } else {
            // پیام دیگران (گیرنده)
            holder.receiverLayout.setVisibility(View.VISIBLE);
            holder.senderLayout.setVisibility(View.GONE);
            holder.receiverMessageText.setText(message.getMessageText());
            holder.receiverTime.setText(message.getTime());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout senderLayout, receiverLayout;
        TextView senderMessageText, senderTime, receiverMessageText, receiverTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderLayout = itemView.findViewById(R.id.sender_layout);
            receiverLayout = itemView.findViewById(R.id.receiver_layout);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            senderTime = itemView.findViewById(R.id.sender_time);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverTime = itemView.findViewById(R.id.receiver_time);
        }
    }
    public void addMessage(Message message) {
        messageList.add(0, message);
        notifyItemInserted(0);
    }

}

