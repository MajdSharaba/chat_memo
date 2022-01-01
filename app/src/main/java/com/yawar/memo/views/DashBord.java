package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.yawar.memo.R;
import com.yawar.memo.fragment.ChatRoomFragment;
import com.yawar.memo.fragment.ProfileFragment;
import com.yawar.memo.fragment.SearchFragment;

public class DashBord extends AppCompatActivity  {

    private ChipNavigationBar navigationBar;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_bord);
        navigationBar = findViewById(R.id.navigationChip);

        if (savedInstanceState == null) {
            navigationBar.setItemSelected(R.id.chat, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, new ChatRoomFragment()).commit();
        }

        navigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {

                    case R.id.chat:
                        fragment = new ChatRoomFragment();
                        break;
                    case  R.id.profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.searchSn:
                        fragment = new SearchFragment();
                        break;
                }

                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, fragment).commit();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}