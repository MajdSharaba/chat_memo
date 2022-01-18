package com.yawar.memo.model;

public class ChatRoomModel {
    public String name;
    public String senderId;
    public String reciverId;
    public String lastMessage;
    public String image;
    public boolean isChecked;
    public  String numberMessage;
    public String  chatId;
    public  String state;



    public ChatRoomModel(String name,String senderId, String reciverId, String lastMessage, String image, boolean isChecked, String numberMessage,String chatId,String state) {

        this.name = name;
        this.senderId = senderId;
        this.reciverId = reciverId;
        this.lastMessage = lastMessage;
        this.image = image;
        this.isChecked = isChecked;
        this.numberMessage= numberMessage;
        this.chatId = chatId;
        this.state = state;
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

    public String getNumberMessage() {
        return numberMessage;
    }

    public void setNumberMessage(String numberMessage) {
        this.numberMessage = numberMessage;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
