package com.yawar.memo.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.yawar.memo.R;
import com.yawar.memo.adapter.UserAdapter;
import com.yawar.memo.model.UserSeen;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    RecyclerView recyclerView;
    List<UserSeen> postList = new ArrayList<>();
    UserAdapter itemAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout,
                container, false);
        recyclerView = v.findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList.add(new UserSeen("sh"));
        postList.add(new UserSeen("sh"));
        postList.add(new UserSeen("sh"));
        postList.add(new UserSeen("sh"));

        itemAdapter = new UserAdapter(postList, getActivity());
        recyclerView.setAdapter(itemAdapter);


        return v;
    }

}


