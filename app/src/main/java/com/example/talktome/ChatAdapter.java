package com.example.talktome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<MyChat> {

    public ChatAdapter(Context context, List<MyChat> arr) {
        super(context, R.layout.adapter_item_chat, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyChat chat = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_item_chat, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        TextView lastMessage = convertView.findViewById(R.id.last_message);
        TextView timestamp = convertView.findViewById(R.id.timestamp);

        name.setText(chat.getName());
        lastMessage.setText(chat.getLastMessage());

        long timeInMillis = chat.getTimestamp();
        String formattedTime = DateFormat.format("MMM dd, yyyy hh:mm", timeInMillis).toString();
        timestamp.setText(formattedTime);

        return convertView;
    }
}
