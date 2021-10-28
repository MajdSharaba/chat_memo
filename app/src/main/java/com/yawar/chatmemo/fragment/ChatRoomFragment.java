package com.yawar.chatmemo.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yawar.chatmemo.R;
import com.yawar.chatmemo.adapter.ChatRoomAdapter;
import com.yawar.chatmemo.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatRoomFragment() {
        // Required empty public constructor
    }
    RecyclerView recyclerView;
    List<ChatRoomModel> data;
    ChatRoomAdapter itemAdapter;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatRoomFragment newInstance(String param1, String param2) {
        ChatRoomFragment fragment = new ChatRoomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = fill_with_data();
       // System.out.println(data.get(1).name);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

       /// ChatRoomAdapter adapter = new ChatRoomAdapter(data,getApp());


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerfragment);
        itemAdapter = new ChatRoomAdapter(data,getContext());


//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(itemAdapter);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemAdapter);



        return view;

    }
    public List<ChatRoomModel> fill_with_data() {

        List<ChatRoomModel> data = new ArrayList<>();
        data.add(new ChatRoomModel("majd", R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd", R.drawable.personimage));
        data.add(new ChatRoomModel("majd", R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd", R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
        data.add(new ChatRoomModel("majd", R.drawable.personimage));

        return data;
    }

}