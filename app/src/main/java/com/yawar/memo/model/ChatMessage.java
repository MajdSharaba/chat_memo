package com.yawar.memo.model;

import android.text.Spannable;
import android.text.SpannableString;

public class ChatMessage {
    public boolean isMe() {
        return isMe;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    private String id;
    private boolean isMe;
    public  Spannable message;
    private String image;
    private  String type;
    private String userId;
    private String dateTime;

    public ChatMessage(String id, boolean isMe, String message, String userId, String dateTime,String type) {
        this.id = id;
        this.isMe = isMe;
        this.message = new SpannableString(message);
        this.userId = userId;
        this.dateTime = dateTime;
        this.type = type;
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
