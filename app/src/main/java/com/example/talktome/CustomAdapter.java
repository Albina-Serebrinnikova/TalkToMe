package com.example.talktome;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MessageViewHolder> {

    private List<String> messageList;

    public  CustomAdapter(List<String> messages) {
        this.messageList = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateMessages(List<String> newMessages) {
        messageList.clear();
        Collections.addAll(newMessages);
        notifyDataSetChanged();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_view_message);
        }

        public void bind(String message) {
            messageTextView.setText(message);
        }
    }
}