package com.example.talktome;

public class ChatMessage {
    private String senderId;
    private String text;
    private long timestamp;

    private ChatMessage() {}

    public ChatMessage(String senderId, String text) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
