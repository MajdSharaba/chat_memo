package com.yawar.chatmemo.model;

import android.text.Spannable;
import android.text.SpannableString;

public class ChatMessage {
    private String id;
    private boolean isMe;
    private Spannable message;
    private String userId;
    private String dateTime;

    public ChatMessage(String id, boolean isMe, String message, String userId, String dateTime) {
        this.id = id;
        this.isMe = isMe;
        this.message = new SpannableString(message);
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public ChatMessage() {

    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public Spannable getMessage() {
        return message;
    }
    public void setMessage(String message) {

        this.message=new SpannableString(message);

    }
    public void setMessage(Spannable message) {

        this.message=message;

    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
