package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.JsonObject;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.ChatRoomModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
     ImageView image;
    Spinner dropdown;

    List<String> spennerItem = new ArrayList<String>();
    String spennerItemChooser;

    EditText edFname,edLname,edEmail;
    ClassSharedPreferences classSharedPreferences;

    Button btnRegister,btnSkip;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    String fName = "USER";
    String lName = "";
    String email = "";
    String userId ;
    String imageString ="";
    ServerApi serverApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initAction();
    }

    private void initView() {
        image = findViewById(R.id.imageProfile);
        edEmail = findViewById(R.id.et_email);
        edFname = findViewById(R.id.et_firstname);
        edLname = findViewById(R.id.et_lastname);
        image = findViewById(R.id.imageProfile);
        btnRegister = findViewById(R.id.btn_Register);
//        btnSkip = findViewById(R.id.btn_skip);
        classSharedPreferences = new ClassSharedPreferences(RegisterActivity.this);
        serverApi = new  ServerApi(RegisterActivity.this);
        dropdown = findViewById(R.id.spinner1);
    }
    private void initAction() {
        spennerItem.add("choose secret number");
        JSONObject jsonObject = classSharedPreferences.getSecretNumbers();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("numbers");
            JSONObject userObject  = jsonObject.getJSONObject("user");
            userId = userObject.getString("id");
            System.out.println(userId+"userId");


            for (int i = 0; i < jsonArray.length(); i++) {
                String item = jsonArray.getString(i);
                spennerItem.add(item);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,spennerItem);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(this);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject.toString()+"majjjjjjjjjjjjjjjjjd");
        ///// get image from gallery
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        ///// register Button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fName = edFname.getText().toString();
                lName = edLname.getText().toString();
                email = edEmail.getText().toString();
                if(CheckAllFields()){

                serverApi.CompleteRegister(fName,lName,email,imageString,spennerItemChooser,userId);
                }
            }
        });

//        btnSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // serverApi.register(fName,lName,email,imageString);
//            }
//        });
    }

    

    private void openGallery() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==Activity.RESULT_OK){
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if(bitmap!=null){
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);}


            image.setImageURI(imageUri);
        }
    }
    private boolean CheckAllFields() {
        char[] chars = fName.toCharArray();
        for(char c : chars){
            if(Character.isDigit(c)){
                edFname.setError("ادخل اسم بلا ارقام");
                return false;
            }

        }
        if (spennerItemChooser.equals("choose secret number")){
            Toast.makeText(this,"please choose secret number",Toast.LENGTH_SHORT).show();
            return false;

        }

        // after all validation return true.
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        spennerItemChooser= adapterView.getItemAtPosition(i).toString();
        System.out.println(spennerItemChooser);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

//    private void postData(String firstname, String lastname) {
//        Retrofit retrofit = apiClient.getClient();
//        apiRest retrofitAPI = retrofit.create(apiRest.class);
////        Retrofit retrofit = apiClient.getClient();
//
//
//        // passing data from our text fields to our modal class.
//        UserModel modal = new UserModel(963);
//
//        // calling a method to create a post and passing our modal class.
//        Call<String> call = retrofitAPI.createPost("963");
//        System.out.println(call.toString()+"majdddddddddddddddddddddddddddd");
//
//        // on below line we are executing our method.
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//
//                // this method is called when we get response from our api.
//                Toast.makeText(LoginOtpInformation.this, "Data added to API", Toast.LENGTH_SHORT).show();
//
//                // below line is for hiding our progress bar.
////                loadingPB.setVisibility(View.GONE);
//
//                // on below line we are setting empty text
//                // to our both edit text.
////                jobEdt.setText("");
////                nameEdt.setText("");
//
//                // we are getting response from our body
//                // and passing it to our modal class.
//                String responseFromAPI = response.body();
//
//                // on below line we are getting our data from modal class and adding it to our string.
//                String responseString = "Response Code : " + response.code() + "\nName : " + responseFromAPI+ "\n" + "Job : " ;
//                System.out.println(responseString);
//
//                // below line we are setting our
//                // string to our text view.
////                responseTV.setText(responseString);
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                System.out.println(call.toString()+"ssssssssssssssssssssss");
//
//                // setting text to our text view when
//                // we get error response from API.
////                responseTV.setText("Error found is : " + t.getMessage());
//            }
//        });
//    }
    
//private void postDataUsingVolley(String firstName, String lastName, String email,Bitmap bitmap) {
//    // url to post our data
//    String url = "http://192.168.1.10:8080/yawar_chat/APIS/signup.php";
//    progressDialog = new ProgressDialog(LoginOtpInformation.this);
//    progressDialog.setMessage("Uploading, please wait...");
//    progressDialog.show();
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//    if(bitmap!=null){
//    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//    byte[] imageBytes = baos.toByteArray();
//     imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);}
//
//
//    // creating a new variable for our request queue
//    RequestQueue queue = Volley.newRequestQueue(LoginOtpInformation.this);
//
//    // on below line we are calling a string
//    // request method to post the data to our API
//    // in this we are calling a post method.
//    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//        @Override
//        public void onResponse(String response) {
//            progressDialog.dismiss();
//
//
////            Toast.makeText(LoginOtpInformation.this, "Data added to API+"+response, Toast.LENGTH_SHORT).show();
//            System.out.println("Data added to API+"+response);
//            try {
//                // on below line we are passing our response
//                // to json object to extract data from it.
//                JSONObject respObj = new JSONObject(response);
//                JSONObject data = respObj.getJSONObject("data");
//                System.out.println(data.getString("first_name"));
//                String user_id = data.getString("id");
//                String first_name = data.getString("first_name");
//                String last_name = data.getString("last_name");
//                String email = data.getString("email");
//                String profile_image = data.getString("profile_image");
//                UserModel userModel = new UserModel(user_id,first_name,last_name,email,"+964 935013485");
//                classSharedPreferences.setUser(userModel);
//                UserModel userModel1 = classSharedPreferences.getUser();
//
//                Intent intent = new Intent(LoginOtpInformation.this,BasicActivity.class);
//                startActivity(intent);
//               System.out.println(userModel1.getUserName()+userModel1.getLastName()+userModel1.getEmail());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }, new com.android.volley.Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            // method to handle errors.
//            Toast.makeText(LoginOtpInformation.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//        }
//    }) {
//        @Override
//        protected Map<String, String> getParams() {
//            // below line we are creating a map for
//            // storing our values in key and value pair.
//            Map<String, String> params = new HashMap<String, String>();
//
//            // on below line we are passing our key
//            // and value pair to our parameters.
//            params.put("phone", classSharedPreferences.getVerficationNumber());
//            params.put("email", email);
//            params.put("first_name", firstName);
//            params.put("last_name", lastName);
//            params.put("picture", imageString);
//
//            // at last we are
//            // returning our params.
//            return params;
//        }
//    };
//    // below line is to make
//    // a json object request.
//    queue.add(request);
//}
}
