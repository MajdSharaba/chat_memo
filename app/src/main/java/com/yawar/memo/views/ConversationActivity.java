package com.yawar.memo.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ChatAdapter;
import com.yawar.memo.model.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ConversationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText messageET;
    private TextView tv_name;
    private  TextView tv_state;
    private ImageView image;
    private ListView messagesContainer;
    private ImageButton sendMessageBtn;
    private ImageButton sendImageBtn;
    private ChatAdapter adapter;
    private  String senderId;
    private  String reciverId;
    private  String userName;
    private  String imageUrl;
    Bitmap bitmap;
    String imageString;

    private static final int PICK_IMAGE = 100;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;

    ArrayList<String> returnValue = new ArrayList<>();
    private  boolean isCoonect;
    private ArrayList<ChatMessage> chatHistory;
    SearchView searchView;
    private Boolean hasConnection = false;
    private Socket socket;
    private Timer timer = new Timer();
    private final long DELAY = 1000;
    {
        try {
            socket = IO.socket("http://192.168.1.10:3000");
        } catch (URISyntaxException e) {}
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        initViews();
        initAction();
        if(savedInstanceState != null){
            hasConnection = savedInstanceState.getBoolean("hasConnection");
        }
        else {
            socket.connect();
            socket.on("connect user", onNewUser);
            socket.on("check connect", check);
            socket.on("on typing", onTyping);
            socket.on("new message", onNewMessage);
            JSONObject userId = new JSONObject();
            try {
                userId.put("user_id",  "2");
                socket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject object = new JSONObject();
        try {
            object.put("my_id",  "2");
            object.put("your_id","1");
            socket.emit("check connect", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.i("kjk", "onCreate: " + hasConnection);
        hasConnection = true;
        }


    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("OnStopppppppppppppppppp");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasConnection", hasConnection);
    }
    ///// for add message
    Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("", "run: ");
                    Log.i("", "run: " + args.length);


                    String text = "";
                    String type = "";
                    try {
                        JSONObject messageJson = new JSONObject(args[0].toString());
                       /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
                        text = messageJson.getString("message");
                        type = messageJson.getString("message_type");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    Toast.makeText(ConversationActivity.this,args[0].toString(),Toast.LENGTH_LONG).show();
                    System.out.println(args[0].toString());
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId("122");
                    if(type.equals("text")){
                    chatMessage.setMessage(text);}
                    else{
                        chatMessage.setImage(text);
                    }
                    chatMessage.setType(type);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(false);
                    displayMessage(chatMessage);


                }
            });
        }
    };


    private void initViews() {

        Bundle bundle = getIntent().getExtras();
        senderId = bundle.getString("sender_id","user");
        reciverId = bundle.getString("reciver_id","user");
        userName = bundle.getString("name","user");
        imageUrl = bundle.getString("image");
//        System.out.println(imageUrl+"userName");
        LinearLayout linearLayout = findViewById(R.id.liner_conversation);
        linearLayout.setBackgroundResource(R.drawable.whatsapp_background_night);
        final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
        // The apps theme is decided depending upon the saved preferences on app startup
        String pref = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value));
        // Comparing to see which preference is selected and applying those theme settings
        if (pref.equals(darkModeValues[0]))
            linearLayout.setBackgroundResource(R.drawable.whatsapp_background);
        if (pref.equals(darkModeValues[1]))
            linearLayout.setBackgroundResource(R.drawable.whatsapp_background_night);
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendMessageBtn =  findViewById(R.id.btn_send_message_text);
        sendImageBtn =  findViewById(R.id.btn_send_message_image);
        searchView = findViewById(R.id.search_con);
        image = findViewById(R.id.image);
        tv_name = findViewById(R.id.name);
        tv_state = findViewById(R.id.state);
        CharSequence charSequence = searchView.getQuery();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        adapter = new ChatAdapter(ConversationActivity.this, new ArrayList<ChatMessage>());
       messagesContainer.setAdapter(adapter);


        loadDummyHistory();
    }

    private void initAction() {
        tv_name.setText(userName);
        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(timer != null){
                    timer.cancel();
            }
//                System.out.println("onTextChange");
                JSONObject onTyping = new JSONObject();
                try {
                    onTyping.put("id", "1");
                    onTyping.put("typing", true);
                    socket.emit("on typing", onTyping);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (charSequence.toString().trim().length() > 0) {
                    sendMessageBtn.setEnabled(true);
                } else {
                    sendMessageBtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>=0) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // TODO: do what you need here (refresh list)
                            // you will probably need to use
                            // runOnUiThread(Runnable action) for some specific
                            // actions
//                            System.out.println(editable+",,,,,,,,,,,,,");
                            JSONObject onTyping = new JSONObject();
                            try {
                                onTyping.put("id", "1");
                                onTyping.put("typing", false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            socket.emit("on typing", onTyping);
                        }


                    }, DELAY);
                }




        }});
//        Glide.with(image.getContext()).load(imageUrl).into(image);
///for send textMessage
        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Pix.start(ConversationActivity.this,  Options.init().setRequestCode(100));
//                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                startActivityForResult(gallery, PICK_IMAGE);
//                ImagePicker.with(ConversationActivity.this)
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                        .start();
                askPermissionAndBrowseFile();


            }
        });
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Options options = Options.init()
//                        .setRequestCode(100)                                           //Request code for activity results
//                        .setCount(3)                                                   //Number of images to restict selection count
//                        .setFrontfacing(false)                                         //Front Facing camera on start
//                        .setPreSelectedUrls(returnValue)                               //Pre selected Image Urls
//                        .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
//                        .setMode(Options.Mode.All)                                     //Option to select only pictures or videos or both
//                        .setVideoDurationLimitinSeconds(30)                            //Duration for video recording
//                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
//                        .setPath("/pix/images");
//                        Pix.start(ConversationActivity.this,  Options.init().setRequestCode(100));
                ///System.out.println("this is image source"+imageString);



                String messageText = messageET.getText().toString();

                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sender_id","2");
                    jsonObject.put("reciver_id", "1");
                    jsonObject.put("message", messageText);
                    jsonObject.put("message_type","text");
                    jsonObject.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId("122");//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                chatMessage.setType("text");
                messageET.setText("");
                displayMessage(chatMessage);
                sendMessage(jsonObject);
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
    private void askPermissionAndBrowseFile()  {
        // With Android Level >= 23, you have to ask the user
        // for permission to access External Storage.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23

            // Check if we have Call permission
            int permisson = ActivityCompat.checkSelfPermission(ConversationActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permisson != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_PERMISSION
                );
                return;
            }
        }
        this.doBrowseFile();
    }
    private void doBrowseFile()  {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i( "log","Permission granted!");
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i("f","Permission denied!");
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }




    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                messagesContainer.setSelection(adapter.getCount()-1);
            }
        });
//        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    //// for get all message

    private void loadDummyHistory(){

        chatHistory = new ArrayList<ChatMessage>();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.10:8000/messagesbyusers", new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
//                System.ouprintln(response);
                try {
                    JSONArray jsonArray = new JSONArray(response);


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject.getString("message")+jsonObject.getString("sender_id"));
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setUserId(jsonObject.getString("id"));
                        chatMessage.setId(jsonObject.getString("sender_id"));
                        chatMessage.setMe(jsonObject.getString("sender_id").equals("2"));
                        if(jsonObject.getString("message_type").equals("text")){
                        chatMessage.setMessage(jsonObject.getString("message"));}
                        else{
                            chatMessage.setImage(jsonObject.getString("message"));
                        }
                        chatMessage.setType(jsonObject.getString("message_type"));
                        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                      chatHistory.add(chatMessage);


//                        chatHistory.add(new ChatMessage(
//                                jsonObject.getString("id"),
//                                jsonObject.getString("sender_id").equals("2"),
//                                jsonObject.getString("message"),
//                                jsonObject.getString("sender_id"),
//                                DateFormat.getDateTimeInstance().format(new Date()),
//                                jsonObject.getString("message_type")

//                        ));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
               // adapter = new ChatAdapter(ConversationActivity.this, chatHistory);
                adapter.add(chatHistory);
                adapter.notifyDataSetChanged();
                scroll();

//                messagesContainer.setAdapter(adapter);
//                scroll();
//                messagesContainer.setAdapter(adapter);
//            for(int i=0; i<chatHistory.size(); i++) {
//            ChatMessage message = chatHistory.get(i);
//            displayMessage(message);
//        }
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
    Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int length = args.length;

                    if(length == 0){
                        return;
                    }
                    //Here i'm getting weird error..................///////run :1 and run: 0

                }
            });
        }
    };
    //// check if anthor user is connect
    Emitter.Listener check = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int length = args.length;
//                    Toast.makeText(getApplicationContext(),args[0].toString(),Toast.LENGTH_SHORT).show();
//                    System.out.println(args[0].toString()+"lllllllllllllllllllll");
                    if(args[0].toString().equals("true")){
                        isCoonect = true;
                        tv_state.setText("متصل الأن");
                        tv_state.setVisibility(View.VISIBLE);
                    }
                    else if(args[0].toString().equals("false")){
                        isCoonect = false;
                        tv_state.setVisibility(View.GONE);
                    }
                    if(length == 0){
                        return;
                    }
                }
            });
        }
    };
    //// for check typing
    Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    System.out.println(args[0].toString()+"lllllllllllllllllllll");
                    if(args[0].toString().equals("true")){
                        tv_state.setText("يكتب الأن");
                        tv_state.setVisibility(View.VISIBLE);
                    }
                    else if (isCoonect){
                        tv_state.setText("متصل الأن");
                    }
                    else {
                        tv_state.setVisibility(View.GONE);
                    }
                }}); }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK ) {
                    if(data != null)  {
                        Uri fileUri = data.getData();
                        Log.i("ff", "Uri: " + fileUri);

                        String filePath = null;
                        try {
//                            filePath = FileUtils.(this,fileUri);
                            System.out.println(fileUri);
                        } catch (Exception e) {
                            Log.e("ll","Error: " + e);
                            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }

        if (resultCode == Activity.RESULT_OK) {
//
            Uri imageUrl = data.getData();
            JSONObject jsonObjec = new JSONObject();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if(bitmap!=null){
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }

            try {
                    jsonObjec.put("sender_id","2");
                    jsonObjec.put("reciver_id", "1");
                    jsonObjec.put("message",imageString );
                    jsonObjec.put("message_type","image");
                    jsonObjec.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId("122");//dummy
                chatMessage.setImage(String.valueOf(imageUrl));
                chatMessage.setType("image");
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                displayMessage(chatMessage);
                socket.emit("new message", jsonObjec);

        }
        }
    //}

//    private void showDialog(Uri imageUrl) {
//        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
//        View mView = LayoutInflater.from(this).inflate(R.layout.dialog_image_picker, null);
//        ImageView photoView = mView.findViewById(R.id.pick_image);
//        photoView.setImageURI(imageUrl);
//
//        mBuilder.setView(mView);
//        AlertDialog mDialog = mBuilder.create();
//        mDialog.show();
//        ImageButton sendImage = mView.findViewById(R.id.btn_img_send);
//        sendImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("i am here");
//                JSONObject jsonObjec = new JSONObject();
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrl);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//                if(bitmap!=null){
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    byte[] imageBytes = baos.toByteArray();
//                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//                }
//
//                try {
//                    jsonObjec.put("sender_id","2");
//                    jsonObjec.put("reciver_id", "1");
//                    jsonObjec.put("message",imageString );
//                    jsonObjec.put("message_type","image");
//                    jsonObjec.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                ChatMessage chatMessage = new ChatMessage();
//                chatMessage.setId("122");//dummy
//                chatMessage.setImage(returnValue.get(0));
//                chatMessage.setType("image");
//                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//                chatMessage.setMe(true);
//
//                displayMessage(chatMessage);
//                sendMessage(jsonObjec);
////                System.out.println(jsonObjec.toString());
////                System.out.println("sendet");
////                socket.emit("new message", jsonObjec);
//                mDialog.cancel();
//
//            }
//        });
//    }

    private void sendMessage(JSONObject chatMessage) {
        System.out.println(chatMessage.toString());
        System.out.println("sendet");
        socket.emit("new message", chatMessage);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(isFinishing()){
            Log.i("", "onDestroy: ");
            System.out.println("disconnect");
            
            socket.disconnect();
            socket.off("new message", onNewMessage);
            socket.off("connect user", onNewUser);
            socket.off("check connect", check);

//            mSocket.off("connect user", onNewUser);
//            mSocket.off("on typing", onTyping);
//            Username = "";
//            messageAdapter.clear();
        }else {
            Log.i("", "onDestroy: is rotating.....");
        }

    }



}


