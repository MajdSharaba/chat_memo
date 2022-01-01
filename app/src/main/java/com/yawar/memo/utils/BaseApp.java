package com.yawar.memo.utils;


import android.app.Application;

import com.yawar.memo.observe.ChatRoomObserve;

public class BaseApp extends Application {

    ChatRoomObserve observeClass;
    @Override
    public void onCreate() {
        super.onCreate();

        observeClass = new ChatRoomObserve();
    }

    public ChatRoomObserve getObserver() {
        return observeClass;
    }

}
