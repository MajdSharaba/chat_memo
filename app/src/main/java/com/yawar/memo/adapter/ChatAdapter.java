package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;


import com.bumptech.glide.Glide;
import com.yawar.memo.R;
import com.yawar.memo.model.ChatMessage;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends BaseAdapter {
    public final List<ChatMessage> chatMessages;
    private Activity context;
    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_chat_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean myMsg = chatMessage.getIsme() ;//Just a dummy check
        //to simulate whether it me or other sender
        setAlignment(holder, myMsg);


        if(chatMessage.getType().equals("text")){

            System.out.println("chatMessage.getType()"+chatMessage.getMessage());
            holder.txtMessage.setText(chatMessage.getMessage());
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.imageMessage.setVisibility(View.GONE);
        }
        else{
            Glide.with(holder.imageMessage.getContext()).load(chatMessage.getImage()).into(holder.imageMessage);
            holder.imageMessage.setVisibility(View.VISIBLE);
            holder.txtMessage.setVisibility(View.GONE);


        }
        holder.txtInfo.setText(chatMessage.getDate());

        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (!isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else {
            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);


            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
        }
    }
    public void filter(String charText) {
        for (ChatMessage wp : chatMessages) {
            if (wp.getMessage() != null) {
                System.out.println(wp.getMessage().toString());
                if (wp.getMessage().toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    int index = wp.getMessage().toString().toLowerCase(Locale.getDefault()).indexOf(charText);

                    Spannable WordtoSpan = new SpannableString(wp.getMessage().toString());
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), index, index + charText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wp.setMessage(WordtoSpan);
                }

                notifyDataSetChanged();
            }
        }
    }


    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.imageMessage= v.findViewById(R.id.imgMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public ImageView imageMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}
