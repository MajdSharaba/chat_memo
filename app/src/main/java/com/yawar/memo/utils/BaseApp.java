package com.yawar.memo.utils;


import android.app.Application;

import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.memo.observe.ChatRoomObserve;
import com.yawar.memo.observe.FireBaseTokenObserve;
import com.yawar.memo.observe.StoriesObserve;

public class BaseApp extends Application {

    ChatRoomObserve observeClass;
    FireBaseTokenObserve fireBaseTokenObserve;
    StoriesObserve storiesObserve;


    @Override
    public void onCreate() {
        super.onCreate();

        observeClass = new ChatRoomObserve();
        fireBaseTokenObserve = new FireBaseTokenObserve();
        storiesObserve = new StoriesObserve();

    }

    public ChatRoomObserve getObserver() {
        return observeClass;
    }
    public FireBaseTokenObserve getForceResendingToken() {
        return fireBaseTokenObserve;
    }
    public StoriesObserve getStoriesObserve() {
        return storiesObserve;
    }


}
