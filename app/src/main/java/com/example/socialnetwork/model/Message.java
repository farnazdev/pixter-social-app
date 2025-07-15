package com.example.socialnetwork.model;

public class Message {
    private String senderId;
    private String messageText;
    private String time;

    public Message(String senderId, String messageText, String time) {
        this.senderId = senderId;
        this.messageText = messageText;
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getTime() {
        return time;
    }
}

