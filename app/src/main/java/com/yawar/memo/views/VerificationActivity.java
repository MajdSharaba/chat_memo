package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.memo.Api.AuthApi;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.utils.BaseApp;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class VerificationActivity extends AppCompatActivity implements Observer {
    Button virvectbtn;
    Button resendbtn;
    ClassSharedPreferences classSharedPreferences;
    private EditText  edtOTP;
    BaseApp myBase;
    int count=60;
    Timer T;
    public PhoneAuthProvider.ForceResendingToken forceResendingToken ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        virvectbtn = findViewById(R.id.btn_verification);
        timer();
        edtOTP = findViewById(R.id.et_verifiction);
        resendbtn = findViewById(R.id.btn_resendCode);
        resendbtn.setEnabled(false);
        myBase = (BaseApp) this.getApplication();
        myBase.getObserver().addObserver(this);
        forceResendingToken =myBase.getForceResendingToken().getForceResendingToken();

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
                resendbtn.setEnabled(false);
                timer();
                System.out.println(classSharedPreferences.getNumber());
                AuthApi authApi = new AuthApi(VerificationActivity.this);
                authApi.resendVerificationCode(classSharedPreferences.getNumber(),forceResendingToken, VerificationActivity.this);

            }
        });
    }
    void timer(){
        T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        resendbtn.setText(count+"");
                        count--;
                        if(count<0){
                            resendbtn.setEnabled(true);
                            resendbtn.setText("RESEND");
                            count=60;
                            T.cancel();


                        }
                    }
                });
            }
        }, 1000, 1000);

    }

    @Override
    public void update(Observable observable, Object o) {
        forceResendingToken =myBase.getForceResendingToken().getForceResendingToken();
        System.out.println(forceResendingToken+"forceResendingToken");
    }
}