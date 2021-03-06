package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.utils.Globale;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    Globale globale;
    ProgressDialog progressDialog;
    TextView tvNumber;
    TextView tvEmail;
    TextView tvSpecialNumber;
    TextView tvStatus;
    ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        setSupportActionBar(findViewById(R.id.toolbar_layout));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        initViews();
        initAction();

    }
    private void initViews() {
        globale = new Globale();
        tvNumber = findViewById(R.id.tv_number);
        tvEmail = findViewById(R.id.tv_email);
        tvSpecialNumber = findViewById(R.id.tv_special_number);
        tvStatus = findViewById(R.id.tv_status);
        fullImage = findViewById(R.id.full_image);

        Bundle bundle = getIntent().getExtras();
        String user_id = bundle.getString("user_id", "Default");
        getUserInfo(user_id);


    }




    private void initAction() {
    }



    private void getUserInfo(String user_id) {
        String url = AllConstants.base_url+"APIS/getuserinfo.php";
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                System.out.println("Data added to API+"+response);
                try {
                    // on below line we are passing our response
                    // to json object to extract data from it.
                    JSONObject respObj = new JSONObject(response);
                    System.out.println(respObj);
                    JSONObject data = respObj.getJSONObject("data");
                    String user_id = data.getString("id");
                    String first_name = data.getString("first_name");
                    String last_name = data.getString("last_name");
                    String email = data.getString("email");
                    String profile_image = data.getString("profile_image");
                    String secret_number = data.getString("sn");
                    String number = data.getString("phone");
                    String status= data.getString("status");

                    //UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                    getSupportActionBar().setTitle(first_name+last_name);

                    tvNumber.setText(number);
                    tvEmail.setText(email);
                    tvStatus.setText(status);
                    tvSpecialNumber.setText(secret_number);
                    if(!profile_image.isEmpty()){
                    Glide.with(fullImage.getContext()).load(profile_image).into(fullImage);}



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(UserDetailsActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.

                params.put("id", user_id);





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