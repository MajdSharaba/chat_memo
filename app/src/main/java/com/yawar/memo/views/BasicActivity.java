package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.GroupSelectorActivity;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ChatAdapter;
import com.yawar.memo.adapter.ChatRoomAdapter;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.Globale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BasicActivity extends AppCompatActivity implements ChatRoomAdapter.CallbackInterfac {
    public static final String ON_CHANGE_DATA_RECEIVER = "android.zeroprojects.mafia.activity.ON_CHANGE_DATA_RECEIVER";
    public static final String ON_SOCKET_CONNECTION = "android.zeroprojects.mafia.activity.ON_SOCKET_CONNECTION";

    private static final String TAG = BasicActivity.class.getSimpleName();

    SwipeableRecyclerView recyclerView;
    List<ChatRoomModel> data;
    List<ChatRoomModel> postList = new ArrayList<>();
    List<ChatRoomModel> archived = new ArrayList<>();

    ChatRoomAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;
    Globale globale;
    ImageButton iBAddArchived;
    LinearLayout linerArchived;
    boolean isArchived= false ;


    public static void start(Context context) {
        Intent starter = new Intent(context, BasicActivity.class);
        context.startActivity(starter);
    }
    private BroadcastReceiver onSocketConnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getExtras().getBoolean("status");

        }
    };
//    @Override
//    protected void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(onSocketConnect, new IntentFilter(ON_SOCKET_CONNECTION));
////        if (adapterRoom != null) adapterRoom.notifyDataSetChanged();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onSocketConnect);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetData();


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//// for full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Intent service = new Intent(this, SocketIOService.class);
        startService(service);
        connectSocket();


        ////// for language
        classSharedPreferences = new ClassSharedPreferences(BasicActivity.this);

        serverApi = new ServerApi(this);
        String lan = classSharedPreferences.getLocale();
        globale = new Globale();
        Locale locale = new Locale(lan);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

//        ///// for dark mode
//
//
//        final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
//        // The apps theme is decided depending upon the saved preferences on app startup
//        String pref = PreferenceManager.getDefaultSharedPreferences(this)
//                .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value));
//        // Comparing to see which preference is selected and applying those theme settings
//        if (pref.equals(darkModeValues[0]))
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        if (pref.equals(darkModeValues[1]))
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        setContentView(R.layout.activity_basic);
//        SharedPreferences prefs = getSharedPreferences("languag", MODE_PRIVATE);
//
//        prefs.edit().putString("lan", "en").commit();

        ////////////for toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Memo");
        setSupportActionBar(toolbar);




//        };
        linerArchived = findViewById(R.id.liner_archived);
        linerArchived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BasicActivity.this, ArchivedActivity.class);

                startActivity(intent);

            }
        });
        //data = fill_with_data();
//        itemAdapter = new ChatRoomAdapter(data,this,listener);

        recyclerView =  findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(itemAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
       // itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);
        itemAdapter = new ChatRoomAdapter(postList,this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
//                mList.remove(position);
//                mAdapter.notifyDataSetChanged();
                System.out.println(position);
//                addToArchived(postList.get(position));
//                postList.remove(position);
//                itemAdapter.notifyDataSetChanged();

            }

            @Override
            public void onSwipedRight(int position) {
                System.out.println(position);
                addToArchived(postList.get(position));
                postList.remove(position);
                itemAdapter.notifyDataSetChanged();
                linerArchived.setVisibility(View.VISIBLE);

            }
        });

        GetData();
//        postList =serverApi.getChatRoom(recyclerView,listener);
//         itemAdapter = new ChatRoomAdapter(postList,BasicActivity.this, listener);
////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
//        recyclerView.setAdapter(itemAdapter);
//        itemAdapter.notifyDataSetChanged();
//        //        itemAdapter.notifyDataSetChanged(); recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
        System.out.println(postList.size());


        ////////////////FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BasicActivity.this, ContactNumberActivity.class);
                startActivity(intent);

            }

        });


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
                Intent intent;

                switch (item.getItemId()) {
                    case R.id.chat:
//                        openFragment(new ChatRoomFragment());
                        return true;

                    case R.id.searchSn:
                         intent = new Intent(BasicActivity.this, SearchActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;


                    case R.id.profile:
                         intent = new Intent(BasicActivity.this, ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;



                    case R.id.calls:
//                        Intent inten = new Intent(BasicActivity.this, ContactNumberActivity.class);
//                        inten.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(inten);


                    case R.id.settings:
                         intent = new Intent(BasicActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;
//                        startActivity(new Intent(BasicActivity.this, SettingsActivity.class));



                }

                return false;
            }
        });
    }



    private void connectSocket() {
        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        startService(service);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.group:
                Intent intent = new Intent(BasicActivity.this, GroupSelectorActivity.class);
                startActivity(intent);

                return true;
            case R.id.item2:
                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
       // fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void GetData() {

        userModel = classSharedPreferences.getUser();
//        System.out.println(userModel.getUserId());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
       // progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, globale.base_url+"APIS/mychat.php?user_id=1", new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
//                progressDialog.dismiss();
                try {
                    JSONObject respObj = new JSONObject(response);
                    System.out.println(respObj);
                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
                    System.out.println(jsonArray);
                    postList.clear();

                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject.getString("last_message"));
                        String image =  jsonObject.getString("image");
                         isArchived =jsonObject.getBoolean("archive");
//                        String imageUrl="";
//                        if(!image.isEmpty()){
//                            imageUrl = globale.base_url+"/uploads/profile/"+image;
//                        }
//                        else{
//                            imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
//                        }

                        postList.add(new ChatRoomModel(
                                jsonObject.getString("username"),
                                jsonObject.getString("sender_id"),
                                jsonObject.getString("reciver_id"),
                                jsonObject.getString("last_message"),
                                image,
                                 false
//                                "https://th.bing.com/th/id/OIP.2s7VxdmHEoDKji3gO_i-5QHaHa?pid=ImgDet&rs=1"

                        ));
                        System.out.println(globale.base_url+"uploads/profile/"+jsonObject.getString("image"));
                    }
                    if(isArchived){
                        linerArchived.setVisibility(View.VISIBLE);


                    }
                    else {
                        linerArchived.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                if(isArchived){
                    linerArchived.setVisibility(View.VISIBLE);
                }
                ///itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);
                itemAdapter = new ChatRoomAdapter(postList, BasicActivity.this);
////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
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
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//    protected void attachBaseContext(Context newBase) {
//        Locale localeToSwitchTo;
//        classSharedPreferences = new ClassSharedPreferences(BasicActivity.this);
//        String lan = classSharedPreferences.getLocale();
//        Locale locale = new Locale(lan);
//        Locale.setDefault(locale);
//
//
//        super.attachBaseContext(MyContextWrapper.wrap(newBase, locale));}


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


//    @Override
//    public void onLongPress(int position, ChatRoomModel chatRoomModel, boolean checked) {
//        System.out.println("add to archived");
//        if(checked){
//            archived.add(chatRoomModel);}
//        else{
//            archived.remove(chatRoomModel);}
//
//
//
//        if(archived.size()>0){
//            iBAddArchived.setVisibility(View.VISIBLE);
//        }
//        else{
//            iBAddArchived.setVisibility(View.INVISIBLE);
//
//        }
//    }

    @Override
    public void onHandleSelection(int position, ChatRoomModel chatRoomModel) {

        Toast.makeText(this, "Position " + chatRoomModel.lastMessage, Toast.LENGTH_SHORT).show();
        System.out.println(chatRoomModel.name);
        Bundle bundle = new Bundle();


        bundle.putString("sender_id", chatRoomModel.senderId);
        bundle.putString("reciver_id",chatRoomModel.reciverId);
        bundle.putString("name",chatRoomModel.name);
        bundle.putString("image",chatRoomModel.getImage());


        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }


    private void addToArchived(ChatRoomModel chatRoomModel) {
        System.out.println(chatRoomModel.lastMessage);
        final ProgressDialog progressDialo = new ProgressDialog(this);
        // url to post our data
        String url = "http://192.168.1.9:8000/archivechat";
        progressDialo.setMessage("Uploading, please wait...");
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialo.dismiss();
                        System.out.println("Data added to API+"+response);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(BasicActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("my_id","1" );
                params.put("your_id", chatRoomModel.reciverId);

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }



}
