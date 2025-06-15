package com.example.talktome;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChat {
    private String name;
    private String lastMessage; // последнее сообщение
    private String phoneNumber;
    private long timestamp; // время последнего сообщения
    private static final int PAGE_SIZE = 20; // Количество сообщений на страницу

    public MyChat(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.lastMessage = "";
        this.timestamp = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void sendMessage(String chatId, String senderId, String messageText) {
        CollectionReference messagesRef = FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatId)
                .collection("messages");

        Map<String, Object> message = new HashMap<>();
        message.put("senderId", senderId);
        message.put("text", messageText);
        message.put("timestamp", FieldValue.serverTimestamp());

        messagesRef.add(message).addOnSuccessListener(documentReference -> {
            //  Успех: сообщение отправленно
            setLastMessage(messageText);
            updateChatInFirestore(chatId);
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }

    private void updateChatInFirestore(String chatId) {
        CollectionReference chatRef = FirebaseFirestore.getInstance().collection("chats");

        chatRef.document(chatId)
                .update("lastMessage", lastMessage,
                        "timestamp", timestamp)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Чат обновлён успешно.");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Ошибка обновления чата", e);
                });
    }

    @SuppressLint("NewApi")
    public void listenForMessages(String chatId, CustomAdapter adapter) {
        CollectionReference messagesRef = FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatId)
                .collection("messages");

        messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // обработка ошибки
                        Log.e("MyChat", "Ошибка получения сообщений: ", e);
                        return;
                    }

                    List<String> messages = new ArrayList<>();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            ChatMessage message = doc.toObject(ChatMessage.class);
                            messages.add(message.getText());
                        }
                        adapter.updateMessages(messages);
                    }
                });
    }

    @SuppressLint("NewApi")
    public void fetchNextPageOfMessages(String chatId, final CustomAdapter adapter, final int pageNumber) {
        CollectionReference messagesRef = FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatId)
                .collection("messages");

        Query query = messagesRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE * pageNumber + PAGE_SIZE);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> messages = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ChatMessage message = document.toObject(ChatMessage.class);
                            messages.add(message.getText());
                        }

                        // Обновляем адаптер новыми сообщениями
                        adapter.updateMessages(messages);
                    } else {
                        Log.e("MyChat", "Ошибка загрузки страницы:", task.getException());
                    }
                });
    }

    public void createCall(String callerId, String receiverId) {
        Map<String, Object> callDate = new HashMap<>();
        callDate.put("callerId", callerId);
        callDate.put("receiverId", receiverId);
        callDate.put("status", "pending");

        FirebaseFirestore.getInstance()
                .collection("calls")
                .add(callDate)
                .addOnSuccessListener(documentReference -> {
                    // звонок инициирован
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    public void updateCallStatus(String callId, String status) {
        DocumentReference callRef = FirebaseFirestore.getInstance().collection("calls").document(callId);
        callRef.update("status", status)
                .addOnSuccessListener(aVoid -> {
                    // Статус обновлён
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }
}
