package com.yawar.memo.observe;

import com.yawar.memo.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ChatRoomObserve extends Observable  {
    List<ChatRoomModel> chatRoomModelList  = new ArrayList<>();
    boolean isArchived= false;

    public List<ChatRoomModel> getChatRoomModelList() {
        return chatRoomModelList;
    }

    public void setChatRoomModelList(List<ChatRoomModel> chatRoomModelList) {
        this.chatRoomModelList = chatRoomModelList;
        setChanged();
        notifyObservers();
    }
    public  void deleteChatRoom(int position){
        chatRoomModelList.remove(position);
        setChanged();
        notifyObservers();
    }
    public  void addChatRoom(ChatRoomModel chatRoomModel){
        chatRoomModelList.add( chatRoomModel);
        setChanged();
        notifyObservers();
    }
    public void setLastMessage(String message , String chatId){
        for(ChatRoomModel chatRoom:chatRoomModelList){
            if(chatRoom.chatId.equals(chatId)){
                chatRoom.setLastMessage(message);
            }
        }
        setChanged();
        notifyObservers();
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
        setChanged();
        notifyObservers();
    }
}
