package com.yawar.memo.interfac;

import android.view.View;

import com.yawar.memo.model.ChatRoomModel;

public interface ListItemClickListener {
    void onClick(View view, ChatRoomModel chatRoomModel);
}
