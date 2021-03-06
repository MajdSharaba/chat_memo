package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ArchivedAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.utils.Globale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.yawar.memo.utils.BaseApp;

public class ArchivedActivity extends AppCompatActivity implements ArchivedAdapter.CallbackInterfac, Observer {

    SwipeableRecyclerView recyclerView;
    List<ChatRoomModel> data;
    List<ChatRoomModel> archived = new ArrayList<>();

    ArchivedAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;
    Globale globale;
    ImageButton iBAddArchived;
    String myId;
    BaseApp myBase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
     
        setContentView(R.layout.activity_archived);
        toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Memo");
        setSupportActionBar(toolbar);
        recyclerView =  findViewById(R.id.recycler_view);
        globale = new Globale();
        classSharedPreferences= new ClassSharedPreferences(this);
        myId = classSharedPreferences.getUser().getUserId();
        myBase = (BaseApp) getApplication();
        myBase.getObserver().addObserver(this);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(itemAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        // itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);

          for(ChatRoomModel chatRoomModel:myBase.getObserver().getChatRoomModelList()){
        if(chatRoomModel.getState().equals("1"))
            archived.add(chatRoomModel);}
        itemAdapter = new ArchivedAdapter(archived,this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
             System.out.println(position);
            }

            @Override
            public void onSwipedRight(int position) {
                System.out.println(position);
                removeFromArchived(archived.get(position));
                archived.remove(position);
                if(archived.size()<1){
                    myBase.getObserver().setArchived(false);
                }
                itemAdapter.notifyDataSetChanged();}
        });
       // GetData();

        itemAdapter.notifyDataSetChanged();
        searchView = findViewById(R.id.search);
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


    }

    private void GetData() {
//        userModel = classSharedPreferences.getUser();
//        System.out.println(userModel.getUserId());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        // progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, AllConstants.base_url+"APIS/my_archive_chat.php?user_id="+myId, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
//                progressDialog.dismiss();
                try {
                    JSONObject respObj = new JSONObject(response);
                    System.out.println(respObj);
                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
                    System.out.println(jsonArray);

                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject.getString("last_message"));
                        String image =  jsonObject.getString("image");
//                        String imageUrl="";
//                        if(!image.isEmpty()){
//                            imageUrl = globale.base_url+"/uploads/profile/"+image;
//                        }
//                        else{
//                            imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
//                        }

                        archived.add(new ChatRoomModel(
                                jsonObject.getString("username"),
                                jsonObject.getString("sender_id"),
                                jsonObject.getString("reciver_id"),
                                jsonObject.getString("last_message"),
                                image,
                                false,
                                 "0",
                                  "0",
                                    "9"
//                                "https://th.bing.com/th/id/OIP.2s7VxdmHEoDKji3gO_i-5QHaHa?pid=ImgDet&rs=1"

                        ));
                        System.out.println(AllConstants.base_url+"uploads/profile/"+jsonObject.getString("image"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                ///itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);
                itemAdapter = new ArchivedAdapter(archived, ArchivedActivity.this);
////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
                recyclerView.setAdapter(itemAdapter);
                itemAdapter.notifyDataSetChanged();
                Toast.makeText(ArchivedActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ArchivedActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

        };
        requestQueue.add(request);
    }

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
    private void removeFromArchived(ChatRoomModel chatRoomModel) {
        System.out.println(chatRoomModel.lastMessage);
        final ProgressDialog progressDialo = new ProgressDialog(this);
        // url to post our data
        String url = "http://192.168.1.12:8000/deletearchive";
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
                myBase.getObserver().setState(chatRoomModel.chatId,"0");


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(ArchivedActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
        archived.clear();
        for(ChatRoomModel chatRoomModel:myBase.getObserver().getChatRoomModelList()){
            if(chatRoomModel.getState().equals("0"))
                archived.add(chatRoomModel);}

    }
}