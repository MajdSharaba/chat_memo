package com.yawar.chatmemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.yawar.chatmemo.R;
import com.yawar.chatmemo.views.UserDetailsActivity;
import com.yawar.chatmemo.interfac.ListItemClickListener;
import com.yawar.chatmemo.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.Collections;
        import java.util.List;
import java.util.Locale;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.View_Holder> {
    final private ListItemClickListener mOnClickListener;

    List<ChatRoomModel> list = Collections.emptyList();
    List<ChatRoomModel> listsearch = new ArrayList<ChatRoomModel>();
    List<ChatRoomModel> listsearch2= new ArrayList<ChatRoomModel>();

    Context context;

    public ChatRoomAdapter(List<ChatRoomModel> data, Context context,  ListItemClickListener mOnClickListener) {
        this.list = data;
        this.context = context;
        this.mOnClickListener = mOnClickListener;
        this.listsearch2.addAll(list);

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
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_custom_layout, null);
                ImageView photoView = mView.findViewById(R.id.imageView);
                photoView.setImageResource(list.get(position).imageId);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
//                Intent intent = new Intent(view.getContext(), UserDetailsActivity.class);
//                view.getContext().startActivity(intent);
//                Toast.makeText(view.getContext(),"Movie Name clicked",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filter(String charText) {
        System.out.println(charText+"kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
        charText = charText.toLowerCase(Locale.getDefault());
            listsearch.clear();
        if (charText.length() == 0) {
            System.out.println(charText+"MMMMMMMMMMMMMMMMMMMMMMMMMMMMM");

            listsearch.addAll(listsearch2);
        } else {
            System.out.println(charText+"MMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
            for (ChatRoomModel wp : listsearch2) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    listsearch.add(wp);
                }
            }
        }
        System.out.println(charText+"LLLLLLLLLLLLLLLLLLLLL");
        list.clear();
        list.addAll(listsearch);
        notifyDataSetChanged();
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

