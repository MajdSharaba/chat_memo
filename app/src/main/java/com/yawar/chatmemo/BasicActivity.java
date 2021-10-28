package com.yawar.chatmemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yawar.chatmemo.fragment.BlankFragment;
import com.yawar.chatmemo.fragment.ChatRoomFragment;

public class BasicActivity extends AppCompatActivity {
//    RecyclerView recyclerView;
//    List<ChatRoomModel> data;
//    ChatRoomAdapter itemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ChatRoomFragment chatRoomFrafment = new ChatRoomFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        openFragment(new ChatRoomFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.chat:
                        openFragment(new ChatRoomFragment());
                        return true;

                    case R.id.addchat:
                        openFragment(new BlankFragment());
                        return true;

                    case R.id.calls:
                        openFragment(new BlankFragment());
                        return true;




                }

                return false;
            }
        });
    }
    void openFragment ( Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }




//        data = fill_with_data();
//        itemAdapter = new ChatRoomAdapter(data,this);
//
//        recyclerView = (RecyclerView) findViewById(R.id.recycler);
//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(itemAdapter);
//        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(linearLayoutManager);

//    public List<ChatRoomModel> fill_with_data() {
//
//        List<ChatRoomModel> data = new ArrayList<>();
//        data.add(new ChatRoomModel("majd", R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd", R.drawable.personimage));
//        data.add(new ChatRoomModel("majd", R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd", R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd",  R.drawable.personimage));
//        data.add(new ChatRoomModel("majd", R.drawable.personimage));
//
//        return data;
//    }
}