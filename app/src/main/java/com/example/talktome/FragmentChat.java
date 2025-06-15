package com.example.talktome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentChat extends Fragment {
    private static final String ARG_PHONE_NUMBER = "phone_number";
    private ListView listChat;
    private List<MyChat> chatList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private ChatViewModel viewModel;

    public static FragmentChat newInstance(String phoneNumber) {
        FragmentChat fragment = new FragmentChat();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE_NUMBER, phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_chat, container, false);
        listChat = v.findViewById(R.id.list_chat);
        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        chatAdapter = new ChatAdapter(getActivity(), chatList);
        listChat.setAdapter(chatAdapter);

        viewModel.getChatList().observe(getViewLifecycleOwner(), this::updateChatList);

        listChat.setOnItemClickListener((adapterView, view, position, id) -> {
           MyChat selectedChat = chatList.get(position);
           openChatIfExists(selectedChat);
        });
        return v;
    }

    private void updateChatList(List<MyChat> newChatList) {
        chatList.clear();
        chatList.addAll(newChatList);
        chatAdapter.notifyDataSetChanged();
    }

    private void openChatIfExists(MyChat chat) {
        if (!chatExists(chat.getPhoneNumber())) {
            createNewChat(chat);
        } else {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("chatnum", chat.getName());
            intent.putExtra("phoneNumber", chat.getPhoneNumber());
            startActivity(intent);
        }
    }

    private boolean chatExists(String phoneNumber) {
        for (MyChat chat : chatList) {
            if (chat.getPhoneNumber().equals(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private void createNewChat(MyChat chat) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chatnum", chat.getName());
        intent.putExtra("phoneNumber", chat.getPhoneNumber());
        startActivity(intent);

        saveChatToFirestore(chat); // Cохранение информации о новом чате

        chatList.add(chat);
        chatAdapter.notifyDataSetChanged();
    }

    private void saveChatToFirestore(MyChat chat) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> chatData = new HashMap<>();
        chatData.put("name", chat.getName());
        chatData.put("phoneNumber", chat.getPhoneNumber());
        chatData.put("lastMessage", chat.getLastMessage());
        chatData.put("timestamp", chat.getTimestamp());

        db.collection("chats")
                .add(chatData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", getString(R.string.chat_successfully_with_id) + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", getString(R.string.error_add_chat), e);
                });
    }
}
