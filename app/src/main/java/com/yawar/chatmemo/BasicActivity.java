package com.yawar.chatmemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yawar.chatmemo.fragment.BlankFragment;
import com.yawar.chatmemo.fragment.ChatRoomFragment;
import com.yawar.chatmemo.utils.ContextUtils;

import java.util.Locale;

public class BasicActivity extends AppCompatActivity {

//    RecyclerView recyclerView;
//    List<ChatRoomModel> data;
//    ChatRoomAdapter itemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
        // The apps theme is decided depending upon the saved preferences on app startup
        String pref = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value));
        // Comparing to see which preference is selected and applying those theme settings
        if (pref.equals(darkModeValues[0]))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (pref.equals(darkModeValues[1]))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);




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

                    case R.id.calls:
                        openFragment(new BlankFragment());
                        return true;


                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    void openFragment ( Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        Locale localeToSwitchTo = new Locale("ar");
//        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
//        super.attachBaseContext(localeUpdatedContext);
//    }


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