package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;


import com.bumptech.glide.Glide;
import com.yawar.memo.R;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.GroupSelectorRespone;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends BaseAdapter {
    public final List<ChatMessage> chatMessages;
    private Activity context;
    private ChatAdapter.CallbackInterface mCallback;
    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * @param groupSelectorRespone - the text to pass back
         */
        void onHandleSelection(int position, ChatMessage groupSelectorRespone,boolean myMessage);
        void onDownload(int position, ChatMessage groupSelectorRespone,boolean myMessage);

    }


    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        try {
            mCallback = (ChatAdapter.CallbackInterface) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
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
//        System.out.println(chatMessage.getState()+"mmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
        setAlignment(holder, myMsg,chatMessage.getState());



        if(chatMessage.getType().equals("text")){

            System.out.println("chatMessage.getType()"+chatMessage.getType()+chatMessage.getMessage());
            holder.txtMessage.setText(chatMessage.getMessage());
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.imageMessage.setVisibility(View.GONE);
            holder.contentFile.setVisibility(View.GONE);
        }
        else if (chatMessage.getType().equals("image")){
            Glide.with(holder.imageMessage.getContext()).load(chatMessage.getImage()).into(holder.imageMessage);
            holder.imageMessage.setVisibility(View.VISIBLE);
            holder.txtMessage.setVisibility(View.GONE);
            holder.contentFile.setVisibility(View.GONE);


        }
        else {
            File pdfFile;
            if(myMsg){
            File d = Environment.getExternalStoragePublicDirectory("memo/send");  // -> filename = maven.pdf
             pdfFile = new File(d, chatMessage.getFileName());}
            else{
                File d = Environment.getExternalStoragePublicDirectory("memo/recive");  // -> filename = maven.pdf
                pdfFile = new File(d, chatMessage.getMessage().toString());}

            System.out.println(pdfFile.exists()+"adapteeeeeeeeeeeeeeeeeeeeer"+chatMessage.getFileName());
            System.out.println(chatMessage.getMessage());
            holder.txtFile.setText(chatMessage.getFileName());
            holder.txtMessage.setVisibility(View.GONE);
            holder.imageMessage.setVisibility(View.GONE);
            holder.contentFile.setVisibility(View.VISIBLE);
            holder.contentFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null) {
                        mCallback.onHandleSelection(position, chatMessages.get(position),myMsg);
                    }
                }
            });
            if(!pdfFile.exists()){
                holder.fileImageButton.setVisibility(View.VISIBLE);

                holder.fileImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null) {
                        mCallback.onDownload(position, chatMessages.get(position),myMsg);
                    }
                }
            });}
            else {
                holder.fileImageButton.setVisibility(View.GONE);

            }
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

    private void setAlignment(ViewHolder holder, boolean isMe,String state) {
        if (isMe) {
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

            holder.txtMessage.setTextColor(context.getResources().getColor(R.color.background_bottom_navigation));
            holder.txtDate.setTextColor(context.getResources().getColor(R.color.background_bottom_navigation));
            if(state.equals("3")){
                holder.imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
            }
            else if(state.equals("2")){
                holder.imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
            }
            else if(state.equals("1")){
                holder.imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
            }
            else{
                holder.imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

            }
            holder.imageSeen.setVisibility(View.VISIBLE);


            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else {
            holder.imageSeen.setVisibility(View.GONE);

            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);
            holder.txtMessage.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.txtDate.setTextColor(context.getResources().getColor(R.color.textColor));








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
        holder.contentFile = (LinearLayout) v.findViewById(R.id.liner_file);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.txtDate = v.findViewById(R.id.tv_date);
        holder.imageSeen = v.findViewById(R.id.iv_state);
        holder.txtFile = v.findViewById(R.id.text_file);
        holder.imageFile = v.findViewById(R.id.image_file);
        holder.fileImageButton = v.findViewById(R.id.image_button_file);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public ImageView imageMessage;
        private  ImageView imageSeen;
        public TextView txtInfo;
        public TextView txtDate;
        public TextView txtFile;
        public ImageView  imageFile;
        ImageButton fileImageButton;


        public LinearLayout content;
        public  LinearLayout contentFile;
        public LinearLayout contentWithBG;
    }
}
