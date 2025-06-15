package com.example.talktome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<MyChat>> chatList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<MyChat>> getChatList() {
        return chatList;
    }

    public void addChat(MyChat chat) {
        List<MyChat> currentList = chatList.getValue();
        if (currentList != null) {
            currentList.add(chat);
            chatList.setValue(currentList);
        }
    }
}
