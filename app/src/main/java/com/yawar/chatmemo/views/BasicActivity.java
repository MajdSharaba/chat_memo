package com.yawar.chatmemo.views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yawar.chatmemo.Api.ClassSharedPreferences;
import com.yawar.chatmemo.Api.ServerApi;
import com.yawar.chatmemo.R;
import com.yawar.chatmemo.adapter.ChatRoomAdapter;
import com.yawar.chatmemo.fragment.BlankFragment;
import com.yawar.chatmemo.interfac.ListItemClickListener;
import com.yawar.chatmemo.model.ChatRoomModel;
import com.yawar.chatmemo.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BasicActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ChatRoomModel> data;
    List<ChatRoomModel> postList = new ArrayList<>();
    ListItemClickListener listener;
    ChatRoomAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//// for full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);


        ////// for language
        classSharedPreferences = new ClassSharedPreferences(BasicActivity.this);
        serverApi = new ServerApi(this);
        String lan = classSharedPreferences.getLocale();
        Locale locale = new Locale(lan);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        ///// for dark mode


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
//        SharedPreferences prefs = getSharedPreferences("languag", MODE_PRIVATE);
//
//        prefs.edit().putString("lan", "en").commit();

        ////////////for toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Memo");
        setSupportActionBar(toolbar);


        ///// for set Adapter
        listener = (view1, position) -> {
            Toast.makeText(this, "Position " + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ConversationActivity.class);

            startActivity(intent);
        };
        //data = fill_with_data();
//        itemAdapter = new ChatRoomAdapter(data,this,listener);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(itemAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        GetData();
//        postList =serverApi.getChatRoom(recyclerView,listener);
//         itemAdapter = new ChatRoomAdapter(postList,BasicActivity.this, listener);
////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
//        recyclerView.setAdapter(itemAdapter);
//        itemAdapter.notifyDataSetChanged();        recyclerView.setAdapter(itemAdapter);
        System.out.println(postList.size());

//        ChatRoomFragment chatRoomFrafment = new ChatRoomFragment();
////////////// for search
        searchView = findViewById(R.id.search);
        CharSequence charSequence = searchView.getQuery();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.filter(newText);
                return false;
            }
        });

/////// for Bottom nav
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.chat);
//        openFragment(new ChatRoomFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.chat:
//                        openFragment(new ChatRoomFragment());
                        return true;

                    case R.id.addchat:
                        Intent intent = new Intent(BasicActivity.this, ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);


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

    void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void GetData() {

        userModel = classSharedPreferences.getUser();
        System.out.println(userModel.getUserId());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "http://192.168.1.10:8080/yawar_chat/APIS/mychat.php?user_id=2", new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
//                System.out.println(response);
                try {
                    JSONObject respObj = new JSONObject(response);
                    System.out.println(respObj);
                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
                    System.out.println(jsonArray);

                    for (int i = 0; i <= jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject.getString("last_message"));

                        postList.add(new ChatRoomModel(
                                jsonObject.getString("username"),
                                jsonObject.getString("sender_id"),
                                jsonObject.getString("reciver_id"),
                                jsonObject.getString("last_message"),
                                "http://192.168.1.10:8080/yawar_chat/uploads/profile/"+jsonObject.getString("image")
//                                "https://th.bing.com/th/id/OIP.2s7VxdmHEoDKji3gO_i-5QHaHa?pid=ImgDet&rs=1"

                        ));
                        System.out.println("http://192.168.1.10:8080/yawar_chat/uploads/profile/"+jsonObject.getString("image"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);
//                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
                recyclerView.setAdapter(itemAdapter);
                itemAdapter.notifyDataSetChanged();
                Toast.makeText(BasicActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(BasicActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

        };
        requestQueue.add(request);
    }

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//    @Override
//    protected void onResume() {
//
//        String lan = authApi.getLocale();
//        System.out.println(lan+"memooooo");
//        Locale locale = new Locale(lan);
//        Locale.setDefault(locale);
//        Resources resources = this.getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//
//        super.onResume();
//    }
    //    @Override
//    protected void attachBaseContext(Context newBase) {
//        Locale localeToSwitchTo;
//
//        SharedPreferences prefs = getSharedPreferences("languag", MODE_PRIVATE);
//
//
//        String lan = prefs.getString("lan","ar");
//
//        if(lan.equals("en")){
//         localeToSwitchTo = new Locale("en");}
//        else{
//             localeToSwitchTo = new Locale("en");}
//        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
//        super.attachBaseContext(localeUpdatedContext);}


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

    public List<ChatRoomModel> fill_with_data() {


            List<ChatRoomModel> data = new ArrayList<>();
//            data.add(new ChatRoomModel("sh", R.drawable.th));
//            data.add(new ChatRoomModel("Ali",  R.drawable.th));
//            data.add(new ChatRoomModel("Mustafa", R.drawable.th));
//            data.add(new ChatRoomModel("fadi", R.drawable.th));
//            data.add(new ChatRoomModel("Mhoo",  R.drawable.th));
//            data.add(new ChatRoomModel("majd",  R.drawable.th));
//            data.add(new ChatRoomModel("majd",  R.drawable.th));
//            data.add(new ChatRoomModel("majd",  R.drawable.th));
//            data.add(new ChatRoomModel("majd",  R.drawable.th));
//            data.add(new ChatRoomModel("majd",  R.drawable.th));
//            data.add(new ChatRoomModel("majd", R.drawable.th));
//            data.add(new ChatRoomModel("majd",  R.drawable.th));
//            data.add(new ChatRoomModel("majd",  R.drawable.th));
//            data.add(new ChatRoomModel("majd", R.drawable.th));

            return data;
        }
}