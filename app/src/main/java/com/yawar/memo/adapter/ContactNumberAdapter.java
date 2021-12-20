package com.yawar.memo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yawar.memo.R;
import com.yawar.memo.model.SendContactNumberResponse;

import java.util.ArrayList;

public class ContactNumberAdapter extends RecyclerView.Adapter<ContactNumberAdapter.ViewHolder> {
    ///Initialize variable
     Activity activity;
     ArrayList<SendContactNumberResponse> arrayList;

    ///Create constructor
    public ContactNumberAdapter(Activity activity, ArrayList<SendContactNumberResponse> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ///Initialize view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_number,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SendContactNumberResponse model = arrayList.get(position);
        holder.tvName.setText(model.getName());
        holder.tvNumber.setText(model.getNumber());
        System.out.println(model.getImage());
        if(!model.getImage().isEmpty()){
            Glide.with(holder.imageView.getContext()).load("http://192.168.1.10:8080/yawar_chat/uploads/profile/"+model.getImage()).into(holder.imageView);}

       // Glide.with(holder.imageView.getContext()).load(model.getImage()).into(holder.imageView);
        if(model.getState().equals("false")){
            holder.button.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ///Initialize variable
        TextView tvName;
        TextView tvNumber;
        ImageView imageView;
        Button button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ////Assign variable
            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
            imageView = itemView.findViewById(R.id.iv_image);
            button = itemView.findViewById(R.id.btn_share);
        }

    }
}
