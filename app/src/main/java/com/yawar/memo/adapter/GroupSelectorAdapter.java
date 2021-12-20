package com.yawar.memo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yawar.memo.R;
import com.yawar.memo.model.GroupSelectorRespone;

import java.util.ArrayList;

public class GroupSelectorAdapter extends RecyclerView.Adapter<com.yawar.memo.adapter.GroupSelectorAdapter.ViewHolders> {
    Activity activity;
    ArrayList<GroupSelectorRespone> arrayList;
    private GroupSelectorAdapter.CallbackInterface mCallback;

    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * @param groupSelectorRespone - the text to pass back
         * @param isChecked - the boolean to pass back
         */
        void onHandleSelection(int position, GroupSelectorRespone groupSelectorRespone,boolean isChecked);
    }


    ///Create constructor
    public GroupSelectorAdapter(Activity activity, ArrayList<GroupSelectorRespone> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        try {
            mCallback = (GroupSelectorAdapter.CallbackInterface) activity;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public com.yawar.memo.adapter.GroupSelectorAdapter.ViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ///Initialize view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_selector, parent, false);
        GroupSelectorAdapter.ViewHolders holder = new GroupSelectorAdapter.ViewHolders(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull com.yawar.memo.adapter.GroupSelectorAdapter.ViewHolders holder, int position) {

        GroupSelectorRespone model = arrayList.get(position);
        holder.tvName.setText(model.getName());
        holder.tvNumber.setText(model.getNumber());
        System.out.println(model.getImage());
         if(!model.getImage().isEmpty()){
        Glide.with(holder.imageView.getContext()).load("http://192.168.1.2:8080/yawar_chat/uploads/profile/"+model.getImage()).into(holder.imageView);}

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onHandleSelection(position, arrayList.get(position), holder.checkBox.isChecked());
                }
            }
        });
//            holder.button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    System.out.println("majdno Erooooor");
//                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
//                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//
//                    contactIntent
//                            .putExtra(ContactsContract.Intents.Insert.NAME, model.getName())
//                            .putExtra(ContactsContract.Intents.Insert.PHONE, model.getPhone());
//
//
//                }
//            });


    }
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//            super.onActivityResult(requestCode, resultCode, intent);
//
//            if (requestCode == 1)
//            {
//                if (resultCode == Activity.RESULT_OK) {
//                    Toast.makeText(this, "Added Contact", Toast.LENGTH_SHORT).show();
//                }
//                if (resultCode == Activity.RESULT_CANCELED) {
//                    Toast.makeText(this, "Cancelled Added Contact", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolders extends RecyclerView.ViewHolder {
        ///Initialize variable
        TextView tvName;
        TextView tvNumber;
        ImageView imageView;

        CheckBox checkBox;

        public ViewHolders(@NonNull View itemView) {
            super(itemView);
            ////Assign variable
            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_status);
            imageView = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.imb_check);
        }
    }
}
