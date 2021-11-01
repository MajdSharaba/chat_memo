package com.yawar.chatmemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yawar.chatmemo.R;
import com.yawar.chatmemo.interfac.ListItemClickListener;
import com.yawar.chatmemo.model.ChatRoomModel;

import java.util.Collections;
        import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.View_Holder> {
    final private ListItemClickListener mOnClickListener;

    List<ChatRoomModel> list = Collections.emptyList();
    Context context;

    public ChatRoomAdapter(List<ChatRoomModel> data, Context context,  ListItemClickListener mOnClickListener) {
        this.list = data;
        this.context = context;
        this.mOnClickListener = mOnClickListener;

    }

    @NonNull
    @Override
    public ChatRoomAdapter.View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_row, parent, false);
        ChatRoomAdapter.View_Holder holder = new View_Holder(v,mOnClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).name);
        holder.imageView.setImageResource(list.get(position).imageId);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

 class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView name;
    ImageView imageView;
     ListItemClickListener mListener;

    View_Holder(View itemView , ListItemClickListener listener) {
        super(itemView);
        mListener = listener;
        name = (TextView) itemView.findViewById(R.id.name);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        itemView.setOnClickListener(this);
    }

     @Override
     public void onClick(View view) {
         mListener.onClick(view, getAdapterPosition());

     }
 }}

