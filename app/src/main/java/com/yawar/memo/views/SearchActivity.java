package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yawar.memo.R;
import com.yawar.memo.adapter.SearchAdapter;
import com.yawar.memo.model.SearchRespone;
import com.yawar.memo.utils.Globale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.CallbackInterface {

    RecyclerView recyclerView;
    SearchView searchView;
    Toolbar toolbar;
    ArrayList<SearchRespone> searchResponeArrayList = new ArrayList<SearchRespone>();
    SearchAdapter searchAdapter;
    BottomNavigationView bottomNavigationView;
    Globale globale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.searchSn);
        globale = new Globale();
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Memo");
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.search_by_secret_number);
        CharSequence charSequence = searchView.getQuery();
        search("");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchResponeArrayList.clear();
                search(newText);
                return false;
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.chat:
//                        openFragment(new ChatRoomFragment());
                        Intent inten = new Intent(SearchActivity.this, BasicActivity.class);
                        inten.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(inten);
                        return true;



                    case R.id.searchSn:
//                        Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
                        return true;

                    case R.id.profile:
                        Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;



                    case R.id.calls:
//                        Intent inten = new Intent(ContactNumberActivity.this, ContactNumberActivity.class);
//                        inten.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(inten);

                    case R.id.settings:
                        intent = new Intent(SearchActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;

                }

                return false;
            }
        });
    }
    private void search(String query) {
        String url = globale.base_url+"/APIS/search_for_user.php";
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                System.out.println("Data added to API+"+response);
                System.out.println("Data added to API+"+response);
                // on below line we are passing our response
                // to json object to extract data from it.
                JSONObject respObj = null;
                try {
                    respObj = new JSONObject(response);
                    JSONArray jsonArray = (JSONArray) respObj.get("data");

                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject.getString("id"));
                        String id = jsonObject.getString("id");
                        String phone = jsonObject.getString("phone");
                        System.out.println(phone);
                        String name = jsonObject.getString("first_name");
                        String secretNumber = jsonObject.getString("sn");
                        String image = jsonObject.getString("image");
//                        String imageUrl="";
//                        if(!image.isEmpty()){
//                            imageUrl = "http://192.168.1.10:8080/yawar_chat/uploads/profile/"+image;
//                        }
//                        else{
//                            imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
//                        }
                        searchResponeArrayList.add(new SearchRespone(id,name,secretNumber,image,phone));
//                        recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
//                        mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
//                        recyclerView.setAdapter(mainAdapter);


                    }
                   recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                    searchAdapter = new SearchAdapter(
                            SearchActivity.this,searchResponeArrayList);
                    recyclerView.setAdapter(searchAdapter);
//                    mainAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                    System.out.println(data.getString("first_name"));
//                    String user_id = data.getString("id");

//                    String last_name = data.getString("last_name");
//                    String email = data.getString("email");
//                    String profile_image = data.getString("profile_image");
//                    UserModel userModel = new UserModel(user_id,first_name,last_name,email,"+964 935013485");
//                    classSharedPreferences.setUser(userModel);
//                    UserModel userModel1 = classSharedPreferences.getUser();
//
//                    Intent intent = new Intent(context, BasicActivity.class);
//                    context.startActivity(intent);
//                    System.out.println(userModel1.getUserName()+userModel1.getLastName()+userModel1.getEmail());

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
                params.put("sn",query);
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
    public void onHandleSelection(int position, SearchRespone searchRespone) {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        contactIntent
                .putExtra(ContactsContract.Intents.Insert.NAME, searchRespone.getName())
                .putExtra(ContactsContract.Intents.Insert.PHONE, searchRespone.getPhone());

       startActivityForResult(contactIntent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Added Contact", Toast.LENGTH_SHORT).show();

                searchAdapter.notifyDataSetChanged();
                return;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                searchAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Cancelled Added Contact", Toast.LENGTH_SHORT).show();
            }
        }
    }
}