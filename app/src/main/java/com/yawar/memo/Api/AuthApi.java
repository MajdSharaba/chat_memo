package com.yawar.memo.Api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.views.RegisterActivity;
import com.yawar.memo.views.VerificationActivity;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
/////this class for Api with firebase
public class AuthApi implements Observer {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    BaseApp myBase;




    public AuthApi(Activity context) {
        this.context = context;
    }

    Activity context;
    ServerApi serverApi;
    private String verificationId;


    private void signInWithCredential(PhoneAuthCredential credential) {
        serverApi = new  ServerApi(context);

        ClassSharedPreferences classSharedPreferences = new ClassSharedPreferences(context);

        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            System.out.println(user.getPhoneNumber()+"phone number");
                            classSharedPreferences.setVerficationNumber(user.getPhoneNumber());
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            serverApi.register();

                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void sendVerificationCode(String number,Activity activity) {
        // this method is used for getting
        // OTP on user phone number.
        System.out.println("problem"+number);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)

                        .setPhoneNumber(number)
                        .setActivity(activity)// Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    public void resendVerificationCode(String phoneNumber,PhoneAuthProvider.ForceResendingToken forceResendingToken,
                                        Activity activity) {
        System.out.println(forceResendingToken);
        SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);

        String name = prefs.getString("verificationid",null);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(forceResendingToken)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    //"+9647510487448"
///7517863790
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String verificationid, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);



            super.onCodeSent(verificationid, forceResendingToken);

            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have alread y created.
            Log.d("nnmn", "onCodeSent:" + verificationid);
//            AllConstants.forceResendingToken=forceResendingToken;
            myBase = (BaseApp)  context.getApplication();
            myBase.getForceResendingToken().addObserver(AuthApi.this);
            myBase.getForceResendingToken().setForceResendingToken(forceResendingToken);
            System.out.println(forceResendingToken);

            prefs.edit().putString("verificationid",verificationid).commit();
            Intent i = new Intent(context, VerificationActivity.class);
            context.startActivity(i);

        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            // final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
//            if (code != null) {
//                // if the code is not null then
//                // we are setting that code to
//                // our OTP edittext field.
////                edtOTP.setText(code);
//
//                // after setting this code
//                // to OTP edittext field we
//                // are calling our verifycode method.
//               /// verifyCode(code,activity);
//            }

        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // below method is use to verify code from Firebase.
    public void verifyCode(String code) {

        // below line is used for getting getting
        // credentials from our verification id and code.
//        Globle globle = new Globle();
//        System.out.println(globle.getVerificationId()+"mnnnnnnnnnnnnnnnnn");
        SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);

        String name = prefs.getString("verificationid",null);
        System.out.println(name+"kkkkkkkkkkkkkkkkkkk");

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(name, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }


    @Override
    public void update(Observable observable, Object o) {

    }
}



