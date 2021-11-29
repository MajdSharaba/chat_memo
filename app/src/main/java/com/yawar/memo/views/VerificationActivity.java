package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yawar.memo.Api.AuthApi;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.R;

public class VerificationActivity extends AppCompatActivity {
    Button virvectbtn;
    Button resendbtn;
    ClassSharedPreferences classSharedPreferences;
    private EditText  edtOTP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        virvectbtn = findViewById(R.id.btn_verification);
        edtOTP = findViewById(R.id.et_verifiction);
        resendbtn = findViewById(R.id.btn_resendCode);
        classSharedPreferences = new ClassSharedPreferences(VerificationActivity.this);

        virvectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtOTP.getText().toString())) {
                    // when mobile number text field is empty
                    // displaying a toast message.
                    Toast.makeText(VerificationActivity.this, "Please enter a valid code.", Toast.LENGTH_SHORT).show();
                } else {
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    AuthApi authApi = new  AuthApi(VerificationActivity.this);
                    authApi.verifyCode(edtOTP.getText().toString());
                }

            }
        });
        resendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(classSharedPreferences.getNumber());
                AuthApi authApi = new AuthApi(VerificationActivity.this);
                authApi.sendVerificationCode(classSharedPreferences.getNumber(), VerificationActivity.this);

            }
        });
    }
}