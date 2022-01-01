package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yawar.memo.R;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.views.UserDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ArchivedAdapter extends RecyclerView.Adapter<ArchivedAdapter.View_Holder> {
//    final private ListItemClickListener mOnClickListener;

    List<ChatRoomModel> list = Collections.emptyList();
    List<ChatRoomModel> listsearch = new ArrayList<ChatRoomModel>();
    List<ChatRoomModel> listsearch2= new ArrayList<ChatRoomModel>();
    public ArchivedAdapter.CallbackInterfac mCallback;
    Activity context;

    public interface CallbackInterfac {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * @param chatRoomModel - the text to pass back
         */
        void onHandleSelection(int position, ChatRoomModel chatRoomModel);

    }


    //public ChatRoomAdapter(List<ChatRoomModel> data, Activity context,  ListItemClickListener mOnClickListener) {
    public ArchivedAdapter(List<ChatRoomModel> data, Activity context) {
        this.list = data;
        this.context = context;
        try {
            mCallback = (ArchivedAdapter.CallbackInterfac) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        this.listsearch2.addAll(list);
        notifyDataSetChanged();


    }

    @NonNull
    @Override
    public ArchivedAdapter.View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_row, parent, false);
        //ChatRoomAdapter.View_Holder holder = new View_Holder(v,mOnClickListener);
        ArchivedAdapter.View_Holder holder = new ArchivedAdapter.View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivedAdapter.View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).name);
        holder.lastMessage.setText(list.get(position).lastMessage);
//        if(!list.get(position).getImage().isEmpty()){
//            Glide.with(holder.imageView.getContext()).load("http://192.168.1.2:8080/yawar_chat/uploads/profile/"+list.get(position).getImage()).into(holder.imageView);}
        //Glide.with(holder.imageView.getContext()).load(list.get(position).getImage()).into(holder.imageView);
        // holder.imageView.setImageResource(list.get(position).imageId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    System.out.println("Llllllllllllllllll");
                    mCallback.onHandleSelection(position, list.get(position));

                }}
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_user_image_layout, null);
                ImageView photoView = mView.findViewById(R.id.imageView);
                Glide.with(photoView.getContext()).load(list.get(position).getImage()).into(photoView);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                ImageButton imageButton = mView.findViewById(R.id.btimg_info);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), UserDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id",list.get(position).getSenderId());
                        intent.putExtras(bundle);
                        view.getContext().startActivity(intent);
                        Toast.makeText(view.getContext(),"Movie Name clicked", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filter(String charText) {
        System.out.println(charText+listsearch2.size());
        charText = charText.toLowerCase(Locale.getDefault());
        listsearch.clear();
        if (charText.length() == 0) {

            listsearch.addAll(listsearch2);
        } else {
            for (ChatRoomModel wp : listsearch2) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    listsearch.add(wp);
                }
            }
        }
        list.clear();
        list.addAll(listsearch);
        notifyDataSetChanged();
    }


    //class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    class View_Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        ImageView imageView;
        LinearLayout linearLayout;
        //ListItemClickListener mListener;

        //View_Holder(View itemView , ListItemClickListener listener) {
        View_Holder(View itemView ) {
            super(itemView);
            //mListener = listener;
            name = (TextView) itemView.findViewById(R.id.name);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            linearLayout = itemView.findViewById(R.id.liner_chat_room_row);

            ///  itemView.setOnClickListener(this);
        }

//     @Override
//     public void onClick(View view) {
//         mListener.onClick(view, list.get(getAdapterPosition()));
//
//     }
    }}