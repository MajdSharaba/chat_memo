package com.yawar.chatmemo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.yawar.chatmemo.R;
import com.yawar.chatmemo.adapter.ChatAdapter;
import com.yawar.chatmemo.model.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConversationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText messageET;
    private TextView tv_name;
    private ImageView image;
    private ListView messagesContainer;
    private ImageButton sendBtn;
    private ChatAdapter adapter;
    private  String senderId;
    private  String reciverId;
    private  String userName;
    private  String imageUrl;
    private ArrayList<ChatMessage> chatHistory;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        initViews();
        initAction();
    }
    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        senderId = bundle.getString("sender_id","user");
        reciverId = bundle.getString("reciver_id","user");
        userName = bundle.getString("name","user");
        imageUrl = bundle.getString("image");
        System.out.println(imageUrl+"userName");
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn =  findViewById(R.id.chatSendButton);
        searchView = findViewById(R.id.search_con);
        image = findViewById(R.id.image);
        tv_name = findViewById(R.id.name);
        CharSequence charSequence = searchView.getQuery();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        loadDummyHistory();
    }

    private void initAction() {
        tv_name.setText(userName);
        Glide.with(image.getContext()).load(imageUrl).into(image);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId("122");//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                messageET.setText("");
                displayMessage(chatMessage);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
    }



    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){

        chatHistory = new ArrayList<ChatMessage>();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.13:8000/messagesbyusers", new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                System.out.println(response);
                try {
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject.getString("message"));

                        chatHistory.add(new ChatMessage(
                                jsonObject.getString("id"),
                                jsonObject.getString("sender_id").equals("2"),
                                jsonObject.getString("message"),
                                jsonObject.getString("sender_id"),
                                DateFormat.getDateTimeInstance().format(new Date())
                        ));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                adapter = new ChatAdapter(ConversationActivity.this, chatHistory);
                messagesContainer.setAdapter(adapter);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();
                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("sender_id",senderId );
                params.put("reciver_id",reciverId );
                // at last we are
                // returning our params.
                return params;
            }

        };
        requestQueue.add(request);


//        adapter = new ChatAdapter(ConversationActivity.this, new ArrayList<ChatMessage>());
//        messagesContainer.setAdapter(adapter);

//        for(int i=0; i<chatHistory.size(); i++) {
//            ChatMessage message = chatHistory.get(i);
//            displayMessage(message);
//        }
    }
}


