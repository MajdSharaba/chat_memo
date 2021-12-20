package com.yawar.memo.model;

public class ChatRoomModel {
    public String name;
    public String senderId;
    public String reciverId;
    public String lastMessage;
    public String image;
    public boolean isChecked;


    public ChatRoomModel(String name,String senderId, String reciverId, String lastMessage, String image, boolean isChecked) {

        this.name = name;
        this.senderId = senderId;
        this.reciverId = reciverId;
        this.lastMessage = lastMessage;
        this.image = image;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReciverId() {
        return reciverId;
    }

    public void setReciverId(String reciverId) {
        this.reciverId = reciverId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
