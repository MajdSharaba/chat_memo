package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ContactNumberAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ContactModel;
import com.yawar.memo.model.SendContactNumberResponse;
import com.yawar.memo.utils.Globale;
import com.yawar.memo.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactNumberActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SearchView searchView;
    Toolbar toolbar;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    ArrayList<SendContactNumberResponse> sendContactNumberResponses = new ArrayList<SendContactNumberResponse>();
    ContactNumberAdapter mainAdapter;
    private Permissions permissions;
    Globale globale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact_number);
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.setSelectedItemId(R.id.calls);
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Memo");
        setSupportActionBar(toolbar);
        globale = new Globale();
        permissions = new Permissions();
        searchView = findViewById(R.id.search_by_secret_number);

        CharSequence charSequence = searchView.getQuery();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
              ///  itemAdapter.filter(newText);
                return false;
            }
        });






//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//
//                    case R.id.chat:
////                        openFragment(new ChatRoomFragment());
//                        Intent inten = new Intent(ContactNumberActivity.this, BasicActivity.class);
//                        inten.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(inten);
//
//                    case R.id.addchat:
//                        Intent intent = new Intent(ContactNumberActivity.this, ProfileActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
//
//
//                    case R.id.calls:
////                        Intent inten = new Intent(ContactNumberActivity.this, ContactNumberActivity.class);
////                        inten.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                        startActivity(inten);
//
//
//                }
//
//                return false;
//            }
//        });
        ///////////////////////////////////
        checkpermission();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.group:
                Intent intent = new Intent(ContactNumberActivity.this, GroupSelectorActivity.class);
                startActivity(intent);

                return true;
            case R.id.item2:
                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void checkpermission() {
        ///check condition
//        if(ContextCompat.checkSelfPermission(ContactNumberActivity.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(ContactNumberActivity.this,new String[]{Manifest.permission.READ_CONTACTS},100);
//        }
//        else {
//            getContactList();
//        }
        if (permissions.isContactOk(this)) {
            getContactList();
        }
        else permissions.requestContact(this);
    }

    private void getContactList() {
         final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri,null,null,null,sort);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
                Cursor phoneCursor = getContentResolver().query(uriPhone, null ,selection , new String[]{id},null);
                if(phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactModel model = new ContactModel();
                    model.setName(name);
                    model.setNumber(number);
                    arrayList.add(model);
                }
            }
            cursor.close();
        }
        System.out.println(arrayList.size());
        sendContactNumber(arrayList);

    }

    private void sendContactNumber(ArrayList<ContactModel> arrayList) {
        String url =AllConstants.base_url+ "APIS/mycontact.php";
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

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
                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
                    System.out.println(jsonArray);

                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                     JSONObject jsonObject = jsonArray.getJSONObject(i);
                     System.out.println(jsonObject.getString("name"));
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    String number = jsonObject.getString("number");
                    String image = jsonObject.getString("image");
//                    String imageUrl="";
//                    if(!image.isEmpty()){
//                        imageUrl = "http://192.168.1.10:8080/yawar_chat/uploads/profile/"+image;
//                    }
//                    else{
//                        imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
//                    }
                    String state = jsonObject.getString("state");
                        sendContactNumberResponses.add(new SendContactNumberResponse(id,name,number,image,state));
//                        recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
//                        mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
//                        recyclerView.setAdapter(mainAdapter);


                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
                    mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
                    recyclerView.setAdapter(mainAdapter);

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
                params.put("data", data);
//                params.put("email", email);
//                params.put("first_name", firstName);
//                params.put("last_name", lastName);
//                params.put("picture", imageString);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            getContactList();
//        }
//        else {
//            Toast.makeText(ContactNumberActivity.this, "permission Denied",Toast.LENGTH_LONG);
//            checkpermission();
//        }
        switch (requestCode) {
            case AllConstants.CONTACTS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContactList();
                } else
                    Toast.makeText(this, "Contact Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
//    private void search(String query) {
//        String url = "http://192.168.1.11:8080/yawar_chat/APIS/search_for_user.php";
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Uploading, please wait...");
//        progressDialog.show();
//
//
//        // creating a new variable for our request queue
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        // on below line we are calling a string
//        // request method to post the data to our API
//        // in this we are calling a post method.
//        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                progressDialog.dismiss();
//
//
////            Toast.makeText(LoginOtpInformation.this, "Data added to API+"+response, Toast.LENGTH_SHORT).show();
//                System.out.println("Data added to API+"+response);
//                System.out.println("Data added to API+"+response);
//                // on below line we are passing our response
//                // to json object to extract data from it.
//                JSONObject respObj = null;
//                try {
//                    respObj = new JSONObject(response);
//                    System.out.println(respObj);
//                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//                    if(jsonArray.length()>0){
//                        sendContactNumberResponses.clear();
////                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
//                    System.out.println(jsonArray);}
//
//                    for (int i = 0; i <= jsonArray.length()-1; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        System.out.println(jsonObject.getString("id"));
//
//                        String name = jsonObject.getString("first_name");
//                        String number = jsonObject.getString("sn");
//                        String image = jsonObject.getString("image");
//                        String imageUrl="";
//                        if(!image.isEmpty()){
//                            imageUrl = "http://192.168.1.11:8080/yawar_chat/uploads/profile/"+image;
//                        }
//                        else{
//                            imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
//                        }
//                        sendContactNumberResponses.add(new SendContactNumberResponse(name,number,imageUrl,"false"));
////                        recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
////                        mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
////                        recyclerView.setAdapter(mainAdapter);
//
//
//                    }
////                    recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
//                    mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
//                    recyclerView.setAdapter(mainAdapter);
////                    mainAdapter.notifyDataSetChanged();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
////                    System.out.println(data.getString("first_name"));
////                    String user_id = data.getString("id");
//
////                    String last_name = data.getString("last_name");
////                    String email = data.getString("email");
////                    String profile_image = data.getString("profile_image");
////                    UserModel userModel = new UserModel(user_id,first_name,last_name,email,"+964 935013485");
////                    classSharedPreferences.setUser(userModel);
////                    UserModel userModel1 = classSharedPreferences.getUser();
////
////                    Intent intent = new Intent(context, BasicActivity.class);
////                    context.startActivity(intent);
////                    System.out.println(userModel1.getUserName()+userModel1.getLastName()+userModel1.getEmail());
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
////                Toast.makeText(this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                params.put("sn",query );
////                params.put("email", email);
////                params.put("first_name", firstName);
////                params.put("last_name", lastName);
////                params.put("picture", imageString);
//
//                // at last we are
//                // returning our params.
//                return params;
//            }
//        };
//        // below line is to make
//        // a json object request.
//        queue.add(request);
//    }

}
