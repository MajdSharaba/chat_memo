package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.utils.Globale;
import com.yawar.memo.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.yawar.memo.utils.BaseApp;

public class IntroActivity extends AppCompatActivity implements Observer {
    ClassSharedPreferences classSharedPreferences;
    Globale globale;
    ProgressDialog progressDialog;
    BaseApp myBase;
    boolean isArchived=false;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private Permissions permissions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        classSharedPreferences = new ClassSharedPreferences(this);
        myBase = (BaseApp) getApplication();
        myBase.getObserver().addObserver(this);
        globale = new Globale();
        permissions = new Permissions();

        //checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        checkPermission();
//
//        createDirectory("memo");
//        createDirectory("memo/send");
//        createDirectory("memo/recive");



    }
    public void GetData() {
        List<ChatRoomModel> postList = new ArrayList<>();


        UserModel userModel = classSharedPreferences.getUser();
        String myId = userModel.getUserId();

       System.out.println(userModel.getUserId());
          progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.GET, AllConstants.base_url+"APIS/mychat.php?user_id="+myId, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject respObj = new JSONObject(response);
                    System.out.println(respObj);
                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
                    System.out.println(jsonArray);
                    postList.clear();

                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //System.out.println(jsonObject.getString("last_message"));
                        String image =  jsonObject.getString("image");
                         isArchived =jsonObject.getBoolean("archive");
                         String username="mustafa";
                            username=jsonObject.getString("username");
                            String state = jsonObject.getString("state");


                        postList.add(new ChatRoomModel(
                                username,
                                jsonObject.getString("sender_id"),
                                jsonObject.getString("reciver_id"),
//                                jsonObject.getString("last_message"),
                                "mlmlml",



                                image,
                                false,
                                 jsonObject.getString("num_msg"),
                                jsonObject.getString("id"),
                                state
//                                "https://th.bing.com/th/id/OIP.2s7VxdmHEoDKji3gO_i-5QHaHa?pid=ImgDet&rs=1"

                        ));
                        System.out.println(AllConstants.base_url+"uploads/profile/"+jsonObject.getString("image"));
                    }
                    if(isArchived){

                        myBase.getObserver().setArchived(true);
                    }
                    System.out.println("postList"+postList.size());
                    myBase.getObserver().setChatRoomModelList(postList);
                    Intent intent = new Intent(IntroActivity.this, DashBord.class);

                    startActivity(intent);
                    IntroActivity.this.finish();
                    System.out.println("myBase.getObserver().getChatRoomModelList().size()"+myBase.getObserver().getChatRoomModelList().size());



//                    else {
//                        linerArchived.setVisibility(View.GONE);
//
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // progressDialog.dismiss();
                }
//                if(isArchived){
//                    linerArchived.setVisibility(View.VISIBLE);
//                }
                ///itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);
//                itemAdapter = new ChatRoomAdapter(postList, BasicActivity.this);
//////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
//                recyclerView.setAdapter(itemAdapter);
//                itemAdapter.notifyDataSetChanged();
//                Toast.makeText(BasicActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressDialog.dismiss();
                Toast.makeText(IntroActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

        };
        requestQueue.add(request);
        System.out.println("postList"+postList.size());
           }

    @Override
    public void update(Observable observable, Object o) {

    }
    public void checkPermission()
    {

        if (permissions.isStorageWriteOk(IntroActivity.this)){
            createDirectory("memo");
            createDirectory("memo/send");
            createDirectory("memo/recive");
            createDirectory("memo/send/voiceRecord");
            createDirectory("memo/recive/voiceRecord");
            createDirectory("memo/send/video");
            createDirectory("memo/recive/video");
            GetData();


        }
        else permissions.requestStorage(IntroActivity.this);
//        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//
//            // Requesting the permission
//            ActivityCompat.requestPermissions(IntroActivity.this, new String[] { permission }, requestCode);
//        }
//        else {
//            Toast.makeText(IntroActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
//            System.out.println("Permission already granted");
//        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {


//        if (requestCode == STORAGE_PERMISSION_CODE) {
//            System.out.println("Permission Granted");
//
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(IntroActivity.this, "Permission Granted", Toast.LENGTH_SHORT) .show();
//            }
//            else {
//                Toast.makeText(IntroActivity.this, "Permission Denied", Toast.LENGTH_SHORT) .show();
//            }
//        }
        switch (requestCode) {
            case AllConstants.STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createDirectory("memo");
                    createDirectory("memo/send");
                    createDirectory("memo/recive");
                    createDirectory("memo/send/voiceRecord");
                    createDirectory("memo/recive/voiceRecord");
                    createDirectory("memo/send/video");
                    createDirectory("memo/recive/video");
                    GetData();
                } else
                    Toast.makeText(this, "Contact Permission denied", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
    }
    void  createDirectory(String dName){
        File yourAppDir = new File(Environment.getExternalStorageDirectory()+File.separator+dName);

        if(!yourAppDir.exists() && !yourAppDir.isDirectory())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Files.createDirectory(Paths.get(yourAppDir.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"problem", Toast.LENGTH_LONG).show();
                }
            } else {
                yourAppDir.mkdir();
            }

        }
        else
        {
            Log.i("CreateDir","App dir already exists");
        }


    }

}

