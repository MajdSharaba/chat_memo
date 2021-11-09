package com.yawar.chatmemo.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.yawar.chatmemo.R;


public class LoginOtpInformation extends AppCompatActivity {
    private ImageView image;
    EditText edFname,edLname,edEmail;
    Button btnRegister;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_otp_information);
        initView();
        initAction();

        image = findViewById(R.id.imageProfile);


    }



    private void initView() {
        image = findViewById(R.id.imageProfile);
        edEmail = findViewById(R.id.et_email);
        edFname = findViewById(R.id.et_firstname);
        edLname = findViewById(R.id.et_lastname);
        image = findViewById(R.id.imageProfile);
        btnRegister = findViewById(R.id.btn_Register);
    }
    private void initAction() {
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
                if (edFname.getText().toString().isEmpty() && edFname.getText().toString().isEmpty()) {
                    Toast.makeText(LoginOtpInformation.this, "Please enter both the values", Toast.LENGTH_SHORT).show();
                    return;
                }

                postData(edFname.getText().toString(), edFname.getText().toString());
            }
        });
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
            image.setImageURI(imageUri);
        }
    }
    private void postData(String firstname, String lastname) {
    }
}