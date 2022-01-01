package com.yawar.memo.Api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.utils.Globale;
import com.yawar.memo.views.IntroActivity;
import com.yawar.memo.views.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerApi {
    Context context;
    ProgressDialog progressDialog;
    ClassSharedPreferences classSharedPreferences;
    Globale globale = new Globale();


    public ServerApi(Context context) {
        this.context = context;
    }

   /// public void register(String firstName, String lastName, String email, String imageString) {
   public void register() {
        classSharedPreferences = new ClassSharedPreferences(context);
        System.out.println( classSharedPreferences.getVerficationNumber()+"classSharedPreferences.getVerficationNumber()");
        // url to post our data
        String url = AllConstants.base_url+"APIS/signup.php";
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                System.out.println("Data added to API+"+response);
                try {
                    // on below line we are passing our response
                    // to json object to extract data from it.
                    JSONObject respObj = new JSONObject(response);
                    JSONObject data = respObj.getJSONObject("data");
                    classSharedPreferences.setSecretNumbers(data);
                    Intent intent = new Intent(context, RegisterActivity.class);
                    context.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(context, "Faield to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();


                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("phone", classSharedPreferences.getVerficationNumber());
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void CompleteRegister(String firstName, String lastName, String email, String imageString,String secretNumber,String userId) {
        classSharedPreferences = new ClassSharedPreferences(context);
        // url to post our data
        String url = AllConstants.base_url+"APIS/completesignup.php";
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();


        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();


//            Toast.makeText(LoginOtpInformation.this, "Data added to API+"+response, Toast.LENGTH_SHORT).show();
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

                    UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                    classSharedPreferences.setUser(userModel);
                    Intent intent = new Intent(context, IntroActivity.class);
                    context.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(context, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("email", email);
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("picture", imageString);
                params.put("sn", secretNumber);
                params.put("id", userId);




                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
    //////////////////////////////////
    public void updateProfile(String firstName, String lastName, String status, String imageString,String userId) {
        classSharedPreferences = new ClassSharedPreferences(context);
        // url to post our data
        String url = AllConstants.base_url+"APIS/updateprofile.php";
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();


        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();


//            Toast.makeText(LoginOtpInformation.this, "Data added to API+"+response, Toast.LENGTH_SHORT).show();
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

                    UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                    classSharedPreferences.setUser(userModel);
                    Intent intent = new Intent(context, IntroActivity.class);
                    context.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(context, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("status", status);
                params.put("first_name", lastName);
                params.put("last_name", firstName);
                params.put("picture", imageString);
                params.put("id", userId);




                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
    ///////////////////////////////////////
    public void createGroup(String name,String imageString,ArrayList<String> arrayList) {

        String url =AllConstants.base_url+ "APIS/addgroup.php";
    ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
    RequestQueue queue = Volley.newRequestQueue(context);

    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            progressDialog.dismiss();

            // on below line we are passing our response
            // to json object to extract data from it.
            JSONObject respObj = null;
            try {
                respObj = new JSONObject(response);
                System.out.println(respObj);
                Intent intent = new Intent(context, IntroActivity.class);
                context.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }, new com.android.volley.Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // method to handle errors.
//                Toast.makeText(this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }
    }) {
        @Override
        protected Map<String, String> getParams() {
            // below line we are creating a map for
            // storing our values in key and value pair.
            Map<String, String> params = new HashMap<String, String>();

            // on below line we are passing our key
            // and value pair to our parameters.
            String data = new Gson().toJson(arrayList);
            params.put("users_id", data);
            params.put("user_id","2");
            params.put("name",name);
//                params.put("email", email);
//                params.put("first_name", firstName);
//                params.put("last_name", lastName);
               params.put("image", imageString);

            // at last we are
            // returning our params.
            return params;
        }
    };
    // below line is to make
    // a json object request.
        queue.add(request);
}
    ////////////////////////////////////////

    public List<ChatRoomModel> GetData() {
        List<ChatRoomModel> postList = new ArrayList<>();
        classSharedPreferences = new ClassSharedPreferences(context);


        UserModel userModel = classSharedPreferences.getUser();
       String myId = userModel.getUserId();
//        System.out.println(userModel.getUserId());
//          progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, AllConstants.base_url+"APIS/mychat.php?user_id="+myId, new Response.Listener<String>() {


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
                       /// isArchived =jsonObject.getBoolean("archive");
//                        String imageUrl="";
//                        if(!image.isEmpty()){
//                            imageUrl = globale.base_url+"/uploads/profile/"+image;
//                        }
//                        else{
//                            imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
//                        }

//                        postList.add(new ChatRoomModel(
//                                jsonObject.getString("username"),
//                                jsonObject.getString("sender_id"),
//                                jsonObject.getString("reciver_id"),
//                                jsonObject.getString("last_message"),
//                                image,
//                                false
////                                "https://th.bing.com/th/id/OIP.2s7VxdmHEoDKji3gO_i-5QHaHa?pid=ImgDet&rs=1"
//
//                        ));
                        System.out.println(AllConstants.base_url+"uploads/profile/"+jsonObject.getString("image"));
                    }
                    System.out.println("postList"+postList.size());

//                    if(isArchived){
//                        linerArchived.setVisibility(View.VISIBLE);
//
//
//                    }
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

        };
        requestQueue.add(request);
        System.out.println("postList"+postList.size());
        if(postList.size()>0){
     return  postList;}
        else {
            return  postList;
        }
    }

}
