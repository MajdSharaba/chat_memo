package com.yawar.memo.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.Globale;
import com.yawar.memo.views.ArchivedActivity;
import com.yawar.memo.views.ContactNumberActivity;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ChatRoomAdapter;
import com.yawar.memo.model.ChatRoomModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.yawar.memo.utils.BaseApp;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomFragment extends Fragment implements ChatRoomAdapter.CallbackInterfac, Observer {

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
//    RecyclerView recyclerView;
//    List<ChatRoomModel> data;
//    ChatRoomAdapter itemAdapter;

    public static final String ON_CHANGE_DATA_RECEIVER = "android.zeroprojects.mafia.activity.ON_CHANGE_DATA_RECEIVER";
    public static final String ON_SOCKET_CONNECTION = "android.zeroprojects.mafia.activity.ON_SOCKET_CONNECTION";
    public static final String ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED";


//    private static final String TAG = BasicActivity.class.getSimpleName();

    SwipeableRecyclerView recyclerView;
    List<ChatRoomModel> data;
    List<ChatRoomModel> postList = new ArrayList<>();
    List<ChatRoomModel> archived = new ArrayList<>();
    String myId;
    BaseApp myBase;

    ChatRoomAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;
    Globale globale;
    ImageButton iBAddArchived;
    LinearLayout linerArchived;
    boolean isArchived;


//    public static void start(Context context) {
//        Intent starter = new Intent(context, BasicActivity.class);
//        context.startActivity(starter);
//    }
    private BroadcastReceiver onSocketConnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getExtras().getBoolean("status");

        }
    };
    private BroadcastReceiver reciveNwMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

                    String objectString = intent.getExtras().getString("message");
                    System.out.println(objectString + "ddddddddddddddddddddddddddd");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(objectString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String text = "";
                    String type = "";
                    String state = "";
                    String senderId = "";
                    String reciverId = "";
                    String id = "";
                    String fileName = "";
                String chatId = "";

            try {

                        /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
                        text = message.getString("message");
                        type = message.getString("message_type");
                        state = message.getString("state");
                        senderId = message.getString("sender_id");
                        id = message.getString("id");
                        reciverId = message.getString("reciver_id");
                        chatId =  message.getString("chat_id");
//                        fileName = message.getString("orginalName");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            myBase.getObserver().setLastMessage(text,chatId);


        }


    };

//    @Override
//    protected void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(onSocketConnect, new IntentFilter(ON_SOCKET_CONNECTION));
////        if (adapterRoom != null) adapterRoom.notifyDataSetChanged();
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(onSocketConnect);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        GetData();
//
//
//    }
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

//        data = fill_with_data();
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

//       ChatRoomAdapter adapter = new ChatRoomAdapter(data,getApp());

////////////////////////////////
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
////        ListItemClickListener listener = (view1, position) -> {
////            Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
////            Intent intent = new Intent(view.getContext(), ConversationActivity.class);
////
////            startActivity(intent);
////        };
//       itemAdapter = new ChatRoomAdapter(data ,  getActivity());
//       // itemAdapter = new ChatRoomAdapter(data , this);
//
//
////        recyclerView.setHasFixedSize(true);
////        recyclerView.setAdapter(itemAdapter);
//        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(itemAdapter);

/////////////////////////



        Intent service = new Intent(getContext(), SocketIOService.class);
       getActivity(). startService(service);
        connectSocket();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(reciveNwMessage, new IntentFilter(ON_MESSAGE_RECEIVED));



        ////// for language
        classSharedPreferences = new ClassSharedPreferences(getContext());

        serverApi = new ServerApi(getContext());
        String lan = classSharedPreferences.getLocale();
        myId = classSharedPreferences.getUser().getUserId();
//        System.out.println("myId"+myId);
        globale = new Globale();
        Locale locale = new Locale(lan);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        myBase = (BaseApp) getActivity().getApplication();
        myBase.getObserver().addObserver(this);
        System.out.println("myBase.getObserver().getChatRoomModelList();"+postList.size());

//        SharedPreferences prefs = getSharedPreferences("languag", MODE_PRIVATE);
//
//        prefs.edit().putString("lan", "en").commit();

        ////////////for toolbar
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Memo");
//        getContext().setSupportActionBar(toolbar);





//        };
        linerArchived = view.findViewById(R.id.liner_archived);
        linerArchived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ArchivedActivity.class);

                startActivity(intent);

            }
        });

        recyclerView =  view.findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        isArchived = myBase.getObserver().isArchived();
        System.out.println("isArchived"+isArchived);

        if(isArchived){
            System.out.println(isArchived);
            linerArchived.setVisibility(View.VISIBLE);

        }

        postList = myBase.getObserver().getChatRoomModelList();

        itemAdapter = new ChatRoomAdapter(postList, this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

                System.out.println(position);
                delete(postList.get(position));
                myBase.getObserver().deleteChatRoom(position);
            }

            @Override
            public void onSwipedRight(int position) {
                System.out.println(position);
                addToArchived(postList.get(position));
                myBase.getObserver().deleteChatRoom(position);
                myBase.getObserver().setArchived(true);

                linerArchived.setVisibility(View.VISIBLE);

            }
        });

//        postList =serverApi.getChatRoom(recyclerView,listener);
//         itemAdapter = new ChatRoomAdapter(postList,BasicActivity.this, listener);
////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
//        recyclerView.setAdapter(itemAdapter);
//        itemAdapter.notifyDataSetChanged();
//        //        itemAdapter.notifyDataSetChanged(); recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
        System.out.println(postList.size());


        ////////////////FloatingActionButton
        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ContactNumberActivity.class);
                startActivity(intent);

            }

        });


//        ChatRoomFragment chatRoomFrafment = new ChatRoomFragment();
////////////// for search
        searchView = view.findViewById(R.id.search);
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


        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(onSocketConnect);
    }

    @Override
    public void onResume() {
        super.onResume();
//        GetData();


    }

//    public List<ChatRoomModel> fill_with_data() {
//
//        List<ChatRoomModel> data = new ArrayList<>();
//       data.add(new ChatRoomModel("sh","1","2","im",null,true));
////        data.add(new ChatRoomModel("majd");
////        data.add(new ChatRoomModel("majd", R.drawable.th));
////        data.add(new ChatRoomModel("majd", R.drawable.th));
////        data.add(new ChatRoomModel("majd",  R.drawable.th));
////        data.add(new ChatRoomModel("majd",  R.drawable.th));
////        data.add(new ChatRoomModel("majd",  R.drawable.th));
////        data.add(new ChatRoomModel("majd",  R.drawable.th));
////        data.add(new ChatRoomModel("majd",  R.drawable.th));
////        data.add(new ChatRoomModel("majd",  R.drawable.th));
////        data.add(new ChatRoomModel("majd", R.drawable.th));
////        data.add(new ChatRoomModel("majd",  R.drawable.th));
////        data.add(new ChatRoomModel("majd",  R.drawable.th));
////        data.add(new ChatRoomModel("majd", R.drawable.th));
//
//        return data;
//    }

    private void connectSocket() {
        Intent service = new Intent(getContext(), SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        getContext().startService(service);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.basic_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id){
//            case R.id.group:
//                Intent intent = new Intent(BasicActivity.this, GroupSelectorActivity.class);
//                startActivity(intent);
//
//                return true;
//            case R.id.item2:
//                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }







    @Override
    public void onHandleSelection(int position, ChatRoomModel chatRoomModel) {
        System.out.println("mmmmmmmmmmmmmmmmmmmajd");

        Toast.makeText(getContext(), "Position " + chatRoomModel.lastMessage, Toast.LENGTH_SHORT).show();
        System.out.println(chatRoomModel.name);
        Bundle bundle = new Bundle();

        if(myId.equals(chatRoomModel.senderId)){
            bundle.putString("reciver_id",chatRoomModel.reciverId);

        }
        else{
            bundle.putString("reciver_id",chatRoomModel.senderId);}

        bundle.putString("sender_id", myId);
//        bundle.putString("reciver_id",chatRoomModel.reciverId);
        bundle.putString("name",chatRoomModel.name);
        bundle.putString("image",chatRoomModel.getImage());


        Intent intent = new Intent(getContext(), ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }


    private void addToArchived(ChatRoomModel chatRoomModel) {
        System.out.println(chatRoomModel.lastMessage);
        final ProgressDialog progressDialo = new ProgressDialog(getContext());
        // url to post our data
        String url = "http://192.168.1.8:8000/archivechat";
        progressDialo.setMessage("Uploading, please wait...");
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("my_id",myId );
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
    private void delete(ChatRoomModel chatRoomModel) {
        System.out.println(chatRoomModel.lastMessage);
        final ProgressDialog progressDialo = new ProgressDialog(getContext());
        // url to post our data
        String url = "http://192.168.1.8:8000/deleteChat";
        progressDialo.setMessage("Uploading, please wait...");
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("my_id",myId );
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


    @Override
    public void update(Observable observable, Object o) {
        System.out.println("mmmmmmmmmmmmm");
        postList=myBase.getObserver().getChatRoomModelList();
        isArchived=myBase.getObserver().isArchived();
        if(isArchived){
            linerArchived.setVisibility(View.VISIBLE);

        }
        else{
            linerArchived.setVisibility(View.GONE);

        }

        System.out.println(postList.size());
        itemAdapter.notifyDataSetChanged();


    }
}