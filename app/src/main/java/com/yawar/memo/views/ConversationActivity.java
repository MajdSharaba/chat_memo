package com.yawar.memo.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.JsonObject;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.BuildConfig;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ChatAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.FileDownloader;
import com.yawar.memo.utils.FilePath;
import com.yawar.memo.utils.FileUtil;
import com.yawar.memo.utils.VolleyMultipartRequest;
import com.yawar.memo.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import io.socket.client.Socket;


public class ConversationActivity extends AppCompatActivity implements ChatAdapter.CallbackInterface {

    private EditText messageET;
    private TextView tv_name;
    private TextView tv_state;
    private ImageView image;
    private RecyclerView messagesContainer;
    private ImageButton sendMessageBtn;
    private ImageButton sendImageBtn;
    private ImageButton deletImageBtn;
    private ChatAdapter adapter;
    LinearLayout view;
    int IMAGE_PICKER_SELECT =600;
    ProgressDialog mProgressDialog;

    boolean viewVisability = false;
    //    private String senderId;
//    private String reciverId;
    private String userName;
    private String imageUrl;
    Bitmap bitmap;
    String imageString;
    MediaController mediaControls;


    String audioPath,audioName;
    String lastSeen="";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String filePath;

    private static final int PICK_IMAGE = 100;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;

    ArrayList<String> returnValue = new ArrayList<>();
    private boolean isCoonect;
    private ArrayList<ChatMessage> chatHistory;
    private ArrayList<ChatMessage> selectedMessage=new ArrayList<>();
    private ArrayList<JSONObject> unSendMessage=new ArrayList<>();
    private ArrayList<String> deleteMessage=new ArrayList<>();




    SearchView searchView;
    private Boolean hasConnection = false;
    private Socket socket;
    private Timer timer = new Timer();
    private final long DELAY = 1000;
    String user_id = "8";
    String anthor_user_id = "9";
    private Permissions permissions;
    private MediaRecorder mediaRecorder;
    RecordView recordView;
    RecordButton recordButton;
    LinearLayout messageLayout;
    LinearLayout personInformationLiner;
    LinearLayout toolsLiner;
    ClassSharedPreferences classSharedPreferences;




    public static final String CHEK = "ConversationActivity.CHECK_CONNECT";
    public static final String TYPING = "ConversationActivity.ON_TYPING";
    public static final String ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED";


    String filepath = "";




    private RequestQueue rQueue;
    private ArrayList<HashMap<String, String>> arraylist;
    private static final String TAG = "MainActivity2";
    LinearLayout imageLiner;
    LinearLayout fileLiner;
    private static final String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    ////////download() Method  PERMISSIONS

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /////////start recive from socket
///// for check if anthor user is connect
    private BroadcastReceiver check = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String check = intent.getExtras().getString("check");
            JSONObject checkObject = null;
            String checkConnect= "false";

            try {
                checkObject = new JSONObject(check);
                checkConnect =  checkObject.getString("is_connect");
                lastSeen = checkObject.getString("last_seen");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(check + "usernnnnnnnnnnnnnnnnnnnnnnn");
            if (checkConnect.equals("true")) {
                isCoonect = true;
                tv_state.setText("متصل الأن");
                tv_state.setVisibility(View.VISIBLE);
            } else if (checkConnect.equals("false")) {
                isCoonect = false;
                tv_state.setText(lastSeen) ;
                tv_state.setVisibility(View.VISIBLE);

//                tv_state.setVisibility(View.GONE);
            }
        }
    };

    private BroadcastReceiver reciveTyping = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String typingString = intent.getExtras().getString("typing");


                    if (typingString.equals("true")) {
                        tv_state.setText("يكتب الأن");
                        tv_state.setVisibility(View.VISIBLE);
                    } else if (isCoonect) {
                        tv_state.setText("متصل الأن");
                    } else {
                        tv_state.setText(lastSeen);


//                        tv_state.setVisibility(View.GONE);
                    }

                }
            });
        }
    };
    private BroadcastReceiver reciveNwMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                    try {

                        /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
                        text = message.getString("message");
                        type = message.getString("message_type");
                        state = message.getString("state");
                        senderId = message.getString("sender_id");
                        id = message.getString("message_id");
                        reciverId = message.getString("reciver_id");
//                        fileName = message.getString("orginalName");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println(senderId + "sender_id+majjjjjjjjjjjd" + fileName);
                    if (senderId.equals(user_id)) {
                        System.out.println("state.equals(\"0000000\")=========================="+user_id+""+senderId+""+state);
//                        Toast.makeText(ConversationActivity.this,args[0].toString(),Toast.LENGTH_LONG).show();


                        for (int i = adapter.chatMessages.size()-1; i > 0; i--) {
                            if (state.equals("3")) {
                                System.out.println("state.equals(\"3\")==========================");

                                if( adapter.chatMessages.get(i).getState().equals("3")){
                                    System.out.println(i+"===============");

                                    break;
                                }
                                adapter.chatMessages.get(i).setState(state);

//                                }

//                            } else if (state.equals("2")) {
//                                if (adapter.chatMessages.get(i).getState().equals("1") || adapter.chatMessages.get(i).getState().equals("0")) {
//                                    adapter.chatMessages.get(i).setState(state);
//                                    adapter.chatMessages.get(i).setId(id);
//                                    System.out.println(adapter.chatMessages.get(i).message);
//                                }
//                                else{
//                                    break;
//                                }
                            } else if (state.equals("2")) {
                                System.out.println("state.equals(\"2\")==========================");

                                if (adapter.chatMessages.get(i).getId().equals(id)) {
                                    adapter.chatMessages.get(i).setState(state);
//                                    adapter.chatMessages.get(i).setId(id);
                                    System.out.println(adapter.chatMessages.get(i).message);
                                }}
                                else if (state.equals("1")) {
                                   /// System.out.println(adapter.chatMessages.get(i).getId()+"xxxx"+id);


                                    if (adapter.chatMessages.get(i).getId().equals(id)) {
                                        System.out.println("majdddddddddddd"+unSendMessage.size());
                                        adapter.chatMessages.get(i).setState(state);
//
//                                        adapter.chatMessages.get(adapter.chatMessages.size()-1).setState(state);
//                                        adapter.chatMessages.get(adapter.chatMessages.size()-1).setId(id);
//                                        for (JSONObject  object:
//                                                unSendMessage) {
                                           for(i=0;i<unSendMessage.size();i++){
                                            try {
                                                if(unSendMessage.get(i).getString("message_id").equals(id))

                                                unSendMessage.remove(unSendMessage.get(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        classSharedPreferences.setList("list",unSendMessage);
                                        break;
                                    }




                            } else break;
//                                if (state.equals("1")) {
//                                unSendMessage.remove(0);
//                                classSharedPreferences.setList("list",unSendMessage);
//
//                                if (adapter.chatMessages.get(i).getState().equals("0")) {
//                                    System.out.println("majdddddddddddd");
//                                    adapter.chatMessages.get(i).setState(state);
//                                    adapter.chatMessages.get(i).setId(id);
////                                    unSendMessage.remove(1);
////                                    classSharedPreferences.setList("list",unSendMessage);
//                                }
//                                else {
//                                    break;
//                                }
//                            }


                        }
//                        if (state.equals("1")) {
//
//
//                                if (adapter.chatMessages.get(adapter.chatMessages.size()-1).getState().equals("0")) {
//                                    System.out.println("majdddddddddddd");
//                                    adapter.chatMessages.get(adapter.chatMessages.size()-1).setState(state);
//                                    adapter.chatMessages.get(adapter.chatMessages.size()-1).setId(id);
////                                    unSendMessage.remove(0);
//                                    classSharedPreferences.setList("list",unSendMessage);
//                                }
//
//                            }

                        adapter.notifyDataSetChanged();
                    } else {
                        JSONObject jsonObject = new JSONObject();


                        try {
                            jsonObject.put("message_id", id);
                            jsonObject.put("sender_id", senderId);
                            jsonObject.put("reciver_id", reciverId);
                            jsonObject.put("message", text);
                            jsonObject.put("message_type", type);
                            jsonObject.put("state", "3");

                            jsonObject.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        onSeen(jsonObject);


                        ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(id);
                        if (type.equals("text")) {
                            chatMessage.setMessage(text);
                        } else if (type.equals("file")||type.equals("voice")||type.equals("video")) {
                            chatMessage.setMessage(text);
                            try {
                                chatMessage.setFileName(message.getString("orginalName"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            chatMessage.setImage(text);
                        }

                        chatMessage.setType(type);
                        chatMessage.setState(state);
                        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                        chatMessage.setMe(false);
                        displayMessage(chatMessage);


                    }
                }
            });
        }
    };
////////////////// end recive from socket

    ////// start send to socket
    private void EnterRoom() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject userEnter = new JSONObject();

        try {
            userEnter.put("my_id", user_id);
            userEnter.put("your_id", anthor_user_id);
            userEnter.put("state", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_ENTER_PARAMTERS, userEnter.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ENTER);
        startService(service);
    }

    ////// check connect
    private void checkConnect() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject object = new JSONObject();
        try {
            object.put("my_id", user_id);
            object.put("your_id", anthor_user_id);
//            socket.emit("check connect", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_CHECK_CONNECT_PARAMTERS, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CHECK_CONNECT);
        startService(service);
    }

    //////////////////// onTyping
    private void onTyping(boolean typing) {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject onTyping = new JSONObject();
        try {
            onTyping.put("id", anthor_user_id);
            onTyping.put("typing", typing);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_TYPING_PARAMTERS, onTyping.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_TYPING);
        startService(service);

    }

    //////////onNewMessage
    private void newMeesage(JSONObject chatMessage) {
        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_NEW_MESSAGE_PARAMTERS, chatMessage.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_MESSAGE);
        startService(service);

    }

    /////////////////onSeen
    private void onSeen(JSONObject chatMessage) {
        Intent service = new Intent(this, SocketIOService.class);

        service.putExtra(SocketIOService.EXTRA_ON_SEEN_PARAMTERS, chatMessage.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_ON_SEEN);
        startService(service);

    }
    ///////////////end


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        LocalBroadcastManager.getInstance(this).registerReceiver(check, new IntentFilter(CHEK));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveTyping, new IntentFilter(TYPING));
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveNwMessage, new IntentFilter(ON_MESSAGE_RECEIVED));
        initViews();
        initAction();
        EnterRoom();
        checkConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(check, new IntentFilter(CHEK));
//        LocalBroadcastManager.getInstance(this).registerReceiver(reciveTyping, new IntentFilter(TYPING));
//        LocalBroadcastManager.getInstance(this).registerReceiver(reciveNwMessage, new IntentFilter(ON_MESSAGE_RECEIVED));


    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("OnStop");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasConnection", hasConnection);
    }


    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("sender_id","1");
        anthor_user_id = bundle.getString("reciver_id","2");
        System.out.println(user_id+anthor_user_id+"mmmmmmmmmmm");
        userName = bundle.getString("name","user");
        imageUrl = bundle.getString("image");
        LinearLayout linearLayout = findViewById(R.id.liner_conversation);
        messagesContainer = findViewById(R.id.messagesContainer);
        messagesContainer.setHasFixedSize(true);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messagesContainer.setLayoutManager(linearLayoutManager);
        adapter = new ChatAdapter(ConversationActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendMessageBtn = findViewById(R.id.btn_send_message_text);
        sendImageBtn = findViewById(R.id.btn_send_message_image);
        searchView = findViewById(R.id.search_con);
        image = findViewById(R.id.image);
        tv_name = findViewById(R.id.name);
        tv_state = findViewById(R.id.state);
        CharSequence charSequence = searchView.getQuery();
        view = findViewById(R.id.dataLayout);
        imageLiner = findViewById(R.id.lytCameraPick);
        fileLiner = findViewById(R.id.pickFile);
        permissions = new Permissions();
        messageLayout = findViewById(R.id.messageLayout);
        personInformationLiner = findViewById(R.id.person_information_liner);
        toolsLiner = findViewById(R.id.tools_liner_layout);


        recordView = (RecordView) findViewById(R.id.recordView);
        recordButton = (RecordButton) findViewById(R.id.recordButton);
        deletImageBtn = findViewById(R.id.image_button_delete);
        recordButton.setRecordView(recordView);

        recordButton.setListenForRecord(false);
        deletImageBtn = findViewById(R.id.image_button_delete);
        classSharedPreferences = new ClassSharedPreferences(this);

        if(classSharedPreferences.getList()!=null){
            unSendMessage = classSharedPreferences.getList();

        }



///// form get message history
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
                if (timer != null) {
                    timer.cancel();
                }
//
                onTyping(true);

                if (charSequence.toString().trim().length() > 0) {
                    sendMessageBtn.setEnabled(true);
                    recordButton.setVisibility(View.GONE);
                    sendMessageBtn.setVisibility(View.VISIBLE);
                } else {
                    sendMessageBtn.setEnabled(false);
                    sendMessageBtn.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 0) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // TODO: do what you need here (refresh list)

                            onTyping(false);
                        }


                    }, DELAY);
                }


            }
        });
///for send textMessage
        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.song);

                if (!viewVisability)
                {System.out.println("show dialog");
                    showLayout();}
                else
                    hideLayout();
            }
            ///////////////////
        });


        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message_id=System.currentTimeMillis()+"_"+user_id;
                System.out.println(System.currentTimeMillis()+"_--"+user_id);

                if(classSharedPreferences.getList()!=null){
                    unSendMessage = classSharedPreferences.getList();

                }
                String messageText = messageET.getText().toString();

                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sender_id", user_id);
                    jsonObject.put("reciver_id", anthor_user_id);
                    jsonObject.put("message", messageText);
                    jsonObject.put("message_type", "text");
                    jsonObject.put("state", "0");
                    jsonObject.put("message_id",message_id);
                    jsonObject.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(message_id);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                chatMessage.setType("text");
                chatMessage.setState("0");
                chatMessage.setChecked(false);
                chatMessage.setId(message_id);
                messageET.setText("");
                displayMessage(chatMessage);
                unSendMessage.clear();

                unSendMessage.add(jsonObject);
//              unSendMessage.clear();




                classSharedPreferences.setList("list",unSendMessage);
                System.out.println(unSendMessage.size()+"sizeeeeeeeeeeeeeeee");
                for (JSONObject  message:
                     unSendMessage) {

                    newMeesage(message);

                }

//                newMeesage(jsonObject);
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
        ////to pick image
        imageLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout();
//                System.out.println("image");
//                ImagePicker.with(ConversationActivity.this)
//                        .crop()                    //Crop image(Optional), Check Customization for more option
//                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
//                        .start();
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/* video/*");
                startActivityForResult(pickIntent,IMAGE_PICKER_SELECT);
            }
        });


        //// to pick file
        fileLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout();
                System.out.println("file");
                askPermissionAndBrowseFile();

            }
        });


        //// for voice record
        recordButton.setOnClickListener(view -> {
            System.out.println("cancccccccc");
            recordButton.setListenForRecord(true);

            if (permissions.isRecordingOk(ConversationActivity.this))
                if (permissions.isStorageReadOk(ConversationActivity.this))
                    recordButton.setListenForRecord(true);
                else permissions.requestStorage(ConversationActivity.this);
            else permissions.requestRecording(ConversationActivity.this);
        });
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");

                setUpRecording();

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                messageLayout.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");

                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists())
                    file.delete();

                recordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);


            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                Log.d("RecordView", "onFinish");

                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                recordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);

///              sendRecodingMessage(audioPath);
                File f = new File(audioPath);

//                System.out.println(Uri.fromFile(f));
                uploadVoice(audioName,Uri.fromFile(f));
           System.out.println(Uri.fromFile(f));
//                ChatMessage chatMessage = new ChatMessage();
//                chatMessage.setId("122");//dummy
//                chatMessage.setMessage(audioPath);
//
//                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//                chatMessage.setMe(true);
//                chatMessage.setType("voice");
//                chatMessage.setState("0");
//                messageET.setText("");
//                displayMessage(chatMessage);


            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");

                mediaRecorder.reset();
                mediaRecorder.release();

                File file = new File(audioPath);
                if (file.exists())
                    file.delete();


                recordView.setVisibility(View.GONE);
                messageLayout.setVisibility(View.VISIBLE);
            }
        });
        deletImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMessage(deleteMessage);

//                System.out.println(selectedMessage.get(0).getMessage()+"kkkkk");
            }
        });


    }

    private void deleteMessage(ArrayList<String> chatMessage) {
        final ProgressDialog progressDialo = new ProgressDialog(this);
        JSONArray jsonArray = new JSONArray(deleteMessage);
       ;
        progressDialo.setMessage("Uploading, please wait...");
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.delete_message, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialo.dismiss();
                System.out.println("Data added to API+"+response);
                for(ChatMessage message:selectedMessage){
                adapter.chatMessages.remove( message);}
                selectedMessage.clear();
                deleteMessage.clear();
//                System.out.println(chatMessage.getMessage()+chatMessage.getId());
                adapter.notifyDataSetChanged();
                toolsLiner.setVisibility(View.GONE);
                personInformationLiner.setVisibility(View.VISIBLE);


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(ConversationActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("data", jsonArray.toString());

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    ///// End initialAction
    private void setUpRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "memo/send/voiceRecord");

        if (!file.exists())
            file.mkdirs();


        audioName = System.currentTimeMillis() + ".mp3";
        audioPath = file.getAbsolutePath() +"/"+ audioName;


        mediaRecorder.setOutputFile(audioPath);
    }
    ////// ask premission for pick file
    private void askPermissionAndBrowseFile()  {
        // With Android Level >= 23, you have to ask the user
        // for permission to access External Storage.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23

            // Check if we have Call permission
            int permisson = ActivityCompat.checkSelfPermission(this,
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
     //// for add message to list and display ie]t
    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }
    /////for browse file from device
    private void doBrowseFile()  {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
    }
    
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

                    Log.i( "lll","Permission granted!");
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i(",,","Permission denied!");
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case AllConstants.RECORDING_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this.permissions.isStorageReadOk(ConversationActivity.this))
                        recordButton.setListenForRecord(true);
                    else this.permissions.requestStorage(ConversationActivity.this);

                } else
                    Toast.makeText(this, "Recording permission denied", Toast.LENGTH_SHORT).show();
                break;
            }
            case AllConstants.STORAGE_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    recordButton.setListenForRecord(true);
                else
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void showLayout() {
        float radius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, view.getLeft(), view.getTop(), 0, radius * 2);
        animator.setDuration(800);
        view.setVisibility(View.VISIBLE);
        viewVisability = true;
        animator.start();

    }

    private void hideLayout() {
        System.out.println("hideLayout");
        float radius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, view.getLeft(), view.getTop(), radius * 2, 0);
        animator.setDuration(800);
        viewVisability = false;
        view.setVisibility(View.INVISIBLE);


        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                System.out.println("View.INVISIBLE)");
                viewVisability = false;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                System.out.println("onAnimationCancel");


            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                System.out.println("onAnimationCancel");

            }
        });



    }

//    @Override
//    public void onBackPressed() {
//
//        if (binding.dataLayout.getVisibility() == View.VISIBLE)
//            hideLayout();
//        else
//            super.onBackPressed();
//    }
///// for scroll to end of list
    private void scroll() {
        messagesContainer.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                if(adapter.getItemCount()>0){
                messagesContainer.scrollToPosition(adapter.getItemCount()-1);}

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
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.load_chat_message, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
//              System.out.println(response);
                try {
                    JSONArray jsonArray = new JSONArray(response);



                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject);
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setUserId(jsonObject.getString("sender_id"));
                        chatMessage.setState(jsonObject.getString("state"));
                        chatMessage.setMe(jsonObject.getString("sender_id").equals(user_id));
                        if(jsonObject.getString("message_type").equals("file")||jsonObject.getString("message_type").equals("voice")||jsonObject.getString("message_type").equals("video")){
                        chatMessage.setFileName(jsonObject.getString("orginalName"));}
//                            chatMessage.setFileName("orginalName");}




                        chatMessage.setId(jsonObject.getString("message_id"));
                        chatMessage.setChecked(false);
                        if(!jsonObject.getString("message_type").equals("image")){
                        chatMessage.setMessage(jsonObject.getString("message"));}
                        else{
                            chatMessage.setImage(jsonObject.getString("message"));
                        }
                        chatMessage.setType(jsonObject.getString("message_type"));
                        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                      chatHistory.add(chatMessage);




                    }

//                    for (JSONObject  jsonObject:
//                            unSendMessage) {
//
//                        ChatMessage chatMessage = new ChatMessage();
//                        chatMessage.setUserId(jsonObject.getString("sender_id"));
//                        chatMessage.setState(jsonObject.getString("state"));
//                        if(jsonObject.getString("message_type").equals("file")||jsonObject.getString("message_type").equals("voice")){
//                            chatMessage.setFileName(jsonObject.getString("orginalName"));}
//
//
//
//                        chatMessage.setId(jsonObject.getString("message_id"));
//                        chatMessage.setChecked(false);
//                        chatMessage.setMe(jsonObject.getString("sender_id").equals(user_id));
//                        System.out.println(jsonObject.getString("sender_id")+""+user_id+"knjkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
//                        if(!jsonObject.getString("message_type").equals("image")){
//                            chatMessage.setMessage(jsonObject.getString("message"));}
//                        else{
//                            chatMessage.setImage(jsonObject.getString("message"));
//                        }
//                        chatMessage.setType(jsonObject.getString("message_type"));
//                        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//                        chatHistory.add(chatMessage);
//                        newMeesage(jsonObject);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
               // adapter = new ChatAdapter(ConversationActivity.this, chatHistory);
                adapter.add(chatHistory);
                adapter.notifyDataSetChanged();
                scroll();



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
                params.put("sender_id",user_id );
                params.put("reciver_id",anthor_user_id );
                System.out.println(params);
                // at last we are
                // returning our params.
                return params;
            }

        };
        requestQueue.add(request);

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String message_id=System.currentTimeMillis()+"_"+user_id;

/////////////////////////////
//        if (resultCode == Activity.RESULT_OK) {
////
//            Uri imageUrl = data.getData();
//            String selectedFilePath = FilePath.getPath(this,imageUrl);
//
//                if(selectedFilePath != null && !selectedFilePath.equals("")){
//                   // tvFileName.setText(selectedFilePath);
//                    System.out.println(selectedFilePath+"mmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
//
////                    File file = new File(selectedFilePath);
////
////                    RequestBody requestFile =
////                            RequestBody.create(MediaType.parse("multipart/form-data"), file);
////                    MultipartBody.Part body =
////                            MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//            JSONObject jsonObjec = new JSONObject();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrl);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//            if(bitmap!=null){
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                byte[] imageBytes = baos.toByteArray();
//                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//            }
//
//            try {
//                    jsonObjec.put("sender_id",user_id);
//                    jsonObjec.put("reciver_id", anthor_user_id);
//                    jsonObjec.put("message",imageString );
//                    jsonObjec.put("state","0");
//
//                jsonObjec.put("message_type","image");
//                    jsonObjec.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                ChatMessage chatMessage = new ChatMessage();
//                chatMessage.setId("122");//dummy
//                chatMessage.setImage(String.valueOf(imageUrl));
//                chatMessage.setType("image");
//                    chatMessage.setState("0");
//
//                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//                chatMessage.setMe(true);
//                displayMessage(chatMessage);
//                socket.emit("new message", jsonObjec);
//
//        }
//
//    }
        /////////////////////////////
        if (requestCode==MY_RESULT_CODE_FILECHOOSER) {
//            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK ) {
//                    if(data != null)  {
//                        Uri fileUri = data.getData();
//                        Log.i("kkk", "Uri: " + fileUri);
//
//                        try {
//                            filePath = FileUtil.getPath(this,fileUri);
////                            upload();
//                            uploadPDF("zzz",filePath)
//
//                            System.out.println(filePath);
//
//                        } catch (Exception e) {
//                            Log.e(",kk","Error: " + e);
//                            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                break;
//        }
                    Uri uri = data.getData();

                    String uriString = uri.toString();



                    File myFile = new File(uriString);

//                        copyFile(myFile,Environment.getExternalStoragePublicDirectory("memo"));

                    String path = myFile.getAbsolutePath();
                    System.out.println(uri+"ppppp"+path);

                    copyFileOrDirectory(FileUtil.getPath(this,uri),Environment.getExternalStoragePublicDirectory("memo/send").getAbsolutePath());

                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                Log.d("nameeeee>>>>  ", displayName);

                               uploadPDF(displayName, uri);
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                        Log.d("nameeeee>>>>  ", displayName);
                    }
                }}
        else{
                    if (resultCode == Activity.RESULT_OK) {
//
                        Uri selectedMediaUri = data.getData();
                        if (selectedMediaUri.toString().contains("image")) {
                            hideLayout();

                            System.out.println("this is image");


                            String selectedFilePath = FilePath.getPath(this, selectedMediaUri);

                            if (selectedFilePath != null && !selectedFilePath.equals("")) {
                                // tvFileName.setText(selectedFilePath);
                                System.out.println(selectedFilePath + "mmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
                                JSONObject jsonObjec = new JSONObject();
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedMediaUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                if (bitmap != null) {
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
                                    byte[] imageBytes = baos.toByteArray();
                                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                }

                                try {
                                    jsonObjec.put("sender_id", user_id);
                                    jsonObjec.put("reciver_id", anthor_user_id);
                                    jsonObjec.put("message", imageString);
                                    jsonObjec.put("state", "0");

                                    jsonObjec.put("message_type", "image");
                                    jsonObjec.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setImage(String.valueOf(selectedMediaUri));
                                chatMessage.setType("image");
                                chatMessage.setState("0");
                                chatMessage.setId(message_id);

                                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                                chatMessage.setMe(true);
                                displayMessage(chatMessage);
//                socket.emit("new message", jsonObjec);
                                ///newMeesage(jsonObjec);

                            }

                        }
                     else  if (selectedMediaUri.toString().contains("video")) {

                            String uriString = selectedMediaUri.toString();
                            File myFile = new File(uriString);

                            String path = myFile.getAbsolutePath();

                            copyFileOrDirectory(FileUtil.getPath(this,selectedMediaUri),Environment.getExternalStoragePublicDirectory("memo/send/video").getAbsolutePath());

                            String displayName = null;

                            if (uriString.startsWith("content://")) {
                                Cursor cursor = null;
                                try {
                                    cursor = this.getContentResolver().query(selectedMediaUri, null, null, null, null);
                                    if (cursor != null && cursor.moveToFirst()) {
                                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                        Log.d("nameeeee>>>>  ", displayName);

                                        uploadVideo(displayName, selectedMediaUri);
                                    }
                                } finally {
                                    cursor.close();
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile.getName();
                                Log.d("nameeeee>>>>  ", displayName);
                            }

//

                    }
                    }
        }

    }
    //// for upload file to server
    private void uploadPDF(final String pdfname, Uri pdffile) {
        String message_id =  System.currentTimeMillis()+"_"+user_id;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(message_id);//dummy
        chatMessage.setMessage(pdffile.toString());
        chatMessage.setFileName(pdfname);

        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(true);
        chatMessage.setType("file");
        chatMessage.setState("0");
        messageET.setText("");
        chatMessage.setChecked(false);
        displayMessage(chatMessage);


        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(pdffile);
            //"file:///storage/emulated/0/memo/1640514470604.3gp"
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_file_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));

//                                String text = "";
//                                String type = "";
//                                String state = "";
//                                String senderId = "";
//                                String reciverId = "";
//                                String id = "";
//
//                                    /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
//                                    text = jsonObject.getString("message");
//                                    type = jsonObject.getString("message_type");
//                                    state = jsonObject.getString("state");
//                                    senderId = jsonObject.getString("sender_id");
//                                    id = jsonObject.getString("id");
//
//                                    reciverId = jsonObject.getString("reciver_id");
//                                    System.out.println(text+type+state+senderId+reciverId+id);
////                                    jsonObject.put("file name",pdfname );
//
//                                newMeesage(jsonObject);
                                JSONObject sendObject = new JSONObject();

                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id",message_id);
                                sendObject.put("chat_id",jsonObject.getString("chat_id"));

                                sendObject.put("orginalName",jsonObject.getString("orginalName"));
                                sendObject.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));


                                newMeesage(sendObject);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id",user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type","file");
                    params.put("state","0");
                    params.put("orginalName",pdfname);
                    params.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("files", new DataPart(pdfname, inputData));

                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ConversationActivity.this);
            rQueue.add(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    ///// for upload voice
    private void uploadVoice(final String voiceName, Uri voicedPath) {
        String message_id =  System.currentTimeMillis()+"_"+user_id;


        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(message_id);//dummy
        chatMessage.setMessage(voicedPath.toString());
        chatMessage.setFileName(voiceName);

        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(true);
        chatMessage.setType("voice");
        chatMessage.setState("0");
        chatMessage.setChecked(false);

        messageET.setText("");
        displayMessage(chatMessage);


        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(voicedPath);
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_Voice_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));
                                JSONObject sendObject = new JSONObject();

                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id",message_id);
                                sendObject.put("orginalName",jsonObject.getString("orginalName"));
                                sendObject.put("chat_id",jsonObject.getString("chat_id"));

                                sendObject.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));


                                newMeesage(sendObject);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id", user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type", "voice");
                    params.put("state", "0");
                    params.put("orginalName", voiceName);
                    params.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("audios", new DataPart(voiceName, inputData));

                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ConversationActivity.this);
            rQueue.add(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //// for upload video
    private void uploadVideo(final String pdfname, Uri pdffile) {
        String message_id =  System.currentTimeMillis()+"_"+user_id;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(message_id);//dummy
        chatMessage.setMessage(pdffile.toString());
        chatMessage.setFileName(pdfname);

        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(true);
        chatMessage.setType("video");
        chatMessage.setState("0");
        messageET.setText("");
        chatMessage.setChecked(false);
        displayMessage(chatMessage);


        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(pdffile);
            //"file:///storage/emulated/0/memo/1640514470604.3gp"
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_video_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));

                                JSONObject sendObject = new JSONObject();

                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id",message_id);
                                sendObject.put("chat_id",jsonObject.getString("chat_id"));

                                sendObject.put("orginalName",jsonObject.getString("orginalName"));
                                sendObject.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));


                                newMeesage(sendObject);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id",user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type","video");
                    params.put("state","0");
                    params.put("orginalName",pdfname);
                    params.put("dateTime", DateFormat.getDateTimeInstance().format(new Date()));
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("vedios", new DataPart(pdfname, inputData));

                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ConversationActivity.this);
            rQueue.add(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
        public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }



    ///////for copy file
    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

///// end copy file


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(check);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveTyping);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveNwMessage);

    }

    //// on click in message
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onHandleSelection(int position, ChatMessage chatMessage,boolean myMessage) {
          System.out.println(chatMessage.message+"onHandleSelection");
        File pdfFile;
        Log.v(TAG, "view() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

            Toast t = Toast.makeText(getApplicationContext(), "You don't have read access !", Toast.LENGTH_LONG);
            t.show();

        } else {
            if(myMessage){
            File d = Environment.getExternalStoragePublicDirectory("memo"+File.separator+"send");  // -> filename = maven.pdf
             pdfFile = new File(d, chatMessage.getFileName());}
            else{
                File d = Environment.getExternalStoragePublicDirectory("memo"+File.separator+"recive");  // -> filename = maven.pdf
                pdfFile = new File(d, chatMessage.getMessage().toString());
            }

            Log.v(TAG, "view() Method pdfFile " + pdfFile.getAbsolutePath());

            Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);

            System.out.println(pdfFile.exists()+"pathhhhhhhhhhhhh");

             if(pdfFile.exists()){
            Log.v(TAG, "view() Method path " + path);

            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ConversationActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        }
             else {
                 Toast.makeText(ConversationActivity.this, "please download file", Toast.LENGTH_SHORT).show();

             }
        }

        Log.v(TAG, "view() Method completed ");



    }
   ///// on click on download
    @Override
    public void downloadFile(int position, ChatMessage chatMessage, boolean myMessage) {
        File pdfFile;
        System.out.println(chatMessage.message+"onDownload");

        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if(myMessage) {
                File d = Environment.getExternalStoragePublicDirectory("memo/send");

                pdfFile = new File(d, chatMessage.getFileName());
                if (!pdfFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                    new DownloadFile().execute(AllConstants.download_url+"files/" + chatMessage.getMessage().toString(), chatMessage.getFileName(),"send");
                } else {
                    Log.v(TAG, "File already download ");

                }
            }
            else {

                File d = Environment.getExternalStoragePublicDirectory("memo/recive");

                pdfFile = new File(d, chatMessage.getMessage().toString());
                if (!pdfFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                    new DownloadFile().execute(AllConstants.download_url+"files/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(),"recive");
                } else {
                    Log.v(TAG, "File already download ");

                }
            }

        }

        Log.v(TAG, "download() Method completed ");

    }

    @Override
    public void downloadVoice(int position, ChatMessage chatMessage, boolean myMessage) {
        File audioFile;
        System.out.println(chatMessage.message+"onDownload");

        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if(myMessage) {
                File d = Environment.getExternalStoragePublicDirectory("memo/send/voiceRecord");

                audioFile = new File(d, chatMessage.getFileName());
                if (!audioFile.exists()) {
                    System.out.println(chatMessage.message.toString()+"kkkkkkkkkk");

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                    //new DownloadFile().execute("http://192.168.1.9:8080/src/yawar_chat/uploads/files/" + chatMessage.getMessage().toString(), chatMessage.getFileName(),"send");
                    new DownloadFile().execute(AllConstants.download_url+"audio/" + chatMessage.getMessage().toString(), chatMessage.getFileName(),"send/voiceRecord");

                } else {
                    MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(audioFile.getAbsolutePath()));
                    mediaPlayer.start();
                    Log.v(TAG, "File already download ");
                    System.out.println("File already download ");

                }
            }
            else {

                File d = Environment.getExternalStoragePublicDirectory("memo/recive/voiceRecord");

                audioFile = new File(d, chatMessage.getMessage().toString());
                if (!audioFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                    new DownloadFile().execute(AllConstants.download_url+"audio/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(),"recive/voiceRecord");
                } else {
                    Log.v(TAG, "File already download ");

                }
            }

        }

        Log.v(TAG, "download() Method completed ");


    }

    @Override
    public void downloadVideo(int position, ChatMessage chatMessage, boolean myMessage) {
        File videoFile;
        System.out.println(chatMessage.message+"onDownload");

        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(ConversationActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();

        } else {

            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            if(myMessage) {
                File d = Environment.getExternalStoragePublicDirectory("memo/send");

                videoFile = new File(d, chatMessage.getFileName());
                if (!videoFile.exists()) {

                    new DownloadFile().execute(AllConstants.download_url+"video/" + chatMessage.getMessage().toString(), chatMessage.getFileName(),"send/video");
                } else {
                    Log.v(TAG, "File already download ");

                }
            }
            else {

                File d = Environment.getExternalStoragePublicDirectory("memo/recive");

                videoFile = new File(d, chatMessage.getMessage().toString());
                if (!videoFile.exists()) {

//            new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                    new DownloadFile().execute(AllConstants.download_url+"video/" + chatMessage.getMessage().toString(), chatMessage.getMessage().toString(),"recive/video");
                } else {
                    Log.v(TAG, "File already download ");

                }
            }

        }

        Log.v(TAG, "download() Method completed ");


    }

    @Override
    public void onLongClick(int position, ChatMessage chatMessage, boolean isChecked) {
        System.out.println(isChecked);
        personInformationLiner.setVisibility(View.GONE);
        toolsLiner.setVisibility(View.VISIBLE);

        if(isChecked){
            System.out.println(chatMessage.getMessage()+"getMessage");
            selectedMessage.add(chatMessage);
            deleteMessage.add(chatMessage.getId());
        }
        else {
            selectedMessage.remove(chatMessage);
            deleteMessage.remove(chatMessage.getId());

            if(selectedMessage.size()<1){
                personInformationLiner.setVisibility(View.VISIBLE);
                toolsLiner.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void playVideo(Uri path) {
        Bundle bundle = new Bundle();
        bundle.putString("path",path.toString());
        Intent intent = new Intent(ConversationActivity.this,VideoActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

//        AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//
//        View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_video_player, null);
//        VideoView videoView;
//        videoView = mView.findViewById(R.id.simpleVideoView);
//        videoView.requestFocus();
//
////        if (mediaControls == null) {
////            // create an object of media controller class
////            mediaControls = new MediaController(ConversationActivity.this);
////            mediaControls.setAnchorView(videoView);
////        }
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//                    @Override
//                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//                        /*
//                         * add media controller
//                         */
//                        mediaControls = new MediaController(ConversationActivity.this);
//                        videoView.setMediaController(mediaControls);
//                        /*
//                         * and set its position on screen
//                         */
//                        mediaControls.setAnchorView(videoView);
//                    }
//                });
//            }
//        });
////        mediaControls = new MediaController(this);
//        videoView.setMediaController(mediaControls);
////        mediaControls.setAnchorView(videoView);
//        videoView.setVideoURI(path);
////                // start a video
//        videoView.start();
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                 Toast.makeText(getApplicationContext(), "Thank You...!!!", Toast.LENGTH_LONG).show(); // display a toast when an video is completed
//            }
//        });
//        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                  Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
//                return false;
//            }
//        });
//        mBuilder.setView(mView);
//        AlertDialog mDialog = mBuilder.create();
//        mDialog.show();


    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Log.v(TAG, "doInBackground() Method invoked ");

            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];
            String folderName = strings[2];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
           /// File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File folder = Environment.getExternalStoragePublicDirectory("memo/"+folderName);

            File pdfFile = new File(folder, fileName);
            Log.v(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsolutePath());
            Log.v(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsoluteFile());

            try {
                pdfFile.createNewFile();
                Log.v(TAG, "doInBackground() file created" + pdfFile);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground() error" + e.getMessage());
                Log.e(TAG, "doInBackground() error" + e.getStackTrace());


            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            Log.v(TAG, "doInBackground() file download completed");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();

        }
    }
}







