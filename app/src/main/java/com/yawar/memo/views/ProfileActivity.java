package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.utils.Globale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private ImageView image;
    TextView txName;
    EditText edFname;
    EditText edLname;
    EditText edEmail;
    TextView txnumber;
    EditText edStatus;
    TextView tvspecial;
    Button btnUpdate;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    String fName ;
    String lName ;
    String status ;
    String userId ;
    String imageString = "";
    ServerApi serverApi;



    String name;
    Button logOutBtn;
    UserModel userModel;
    private static final int PICK_IMAGE = 100;
    Uri imageUri ;
    private GoogleApiClient googleApiClient;
    Globale globale ;

    private GoogleSignInOptions gso;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        initAction();}



    private void initAction() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }

        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 fName  = edFname.getText().toString();


                 lName = edLname.getText().toString();

                 status = edStatus.getText().toString();
                 System.out.println(fName+lName+status+userId);


                     serverApi.updateProfile(fName,lName,status,imageString,userId);

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.chat:
                        Intent intent = new Intent(ProfileActivity.this, BasicActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);

//                        openFragment(new ChatRoomFragment());
                        return true;

                    case R.id.searchSn:
                        Intent inten = new Intent(ProfileActivity.this, SearchActivity.class);
                        inten.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(inten);
                        return  true;

                    case R.id.profile:
                        return  true;


                    case R.id.calls:


//                        openFragment(new BlankFragment());
                        return true;

                    case R.id.settings:
                        intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;


                }

                return false;
            }
        });
    }

    private void initView() {
        globale = new Globale();
        serverApi = new ServerApi(this);
        ClassSharedPreferences classSharedPreferences = new ClassSharedPreferences(ProfileActivity.this);
        userModel = classSharedPreferences.getUser();
        userId = userModel.getUserId();
        fName = userModel.getUserName();
        lName = userModel.getLastName();
        status = userModel.getStatus();
        image = findViewById(R.id.image);
        txName = findViewById(R.id.username);
        txnumber = findViewById(R.id.number);
        edFname = findViewById(R.id.et_fName);
        edLname = findViewById(R.id.et_lName);
        edStatus = findViewById(R.id.status);
        tvspecial = findViewById(R.id.textspecial);
        if(!userModel.getUserName().equals(""))
        txName.setHint(userModel.getLastName()+userModel.getUserName());


        txnumber.setHint(userModel.getPhone());
        if(!userModel.getLastName().equals(""))
        edStatus.setText(userModel.getStatus());
        tvspecial.setHint(userModel.getSecretNumber());
         if(!userModel.getUserName().equals(""))

        edFname.setText(userModel.getUserName());
        if(!userModel.getLastName().equals(""))
        edLname.setText(userModel.getLastName());

        btnUpdate = findViewById(R.id.btn_update);

//        if(!userModel.getImage().equals("")||userModel.getImage().equals(null) )
//        Glide.with(image).load(globale.base_url+"uploads/profile/"+userModel.getImage()).into(image);


        ///editText.setHint(userModel.getUserName()+userModel.getLastName());
//        logOutBtn = findViewById(R.id.btn_logout);
//        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        googleApiClient=new GoogleApiClient.Builder(this)
//                .enableAutoManage(this,this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
//                .build();
       bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);
    }

    private void openGallery() {
       /// Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
         ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
//        startActivityForResult( PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode== Activity.RESULT_OK){
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
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
               /// System.out.println(imageString);

            image.setImageURI(imageUri);
        }}}
    private void gotoLoginActivity(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
