package com.yawar.chatmemo.Api;

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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.chatmemo.views.BasicActivity;
import com.yawar.chatmemo.views.VerificationActivity;

import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class AuthApi {
   private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    public AuthApi(Context context) {
        this.context = context;
    }

    Context context;

    private String verificationId;
    private void signInWithCredential(PhoneAuthCredential credential) {

        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            Intent i = new Intent(context, BasicActivity.class);
                            context.startActivity(i);
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
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)

                        .setPhoneNumber("+9647510487448")
                        .setActivity(activity)// Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
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
    public void setName(String name){
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);
        prefs.edit().putString("name",name).commit();
        System.out.println("Memo+"+name);

    }
    public void setNumber(String number){
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);
        prefs.edit().putString("number",number).commit();

    }
    public String getName(){
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);

        String name = prefs.getString("name","UserName");
        return  name;


    }
    public String getNumber(){
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);

        String name = prefs.getString("number","UserName");
        return  name;

    }
    public void setLocale(String lan){
        SharedPreferences prefs = context.getSharedPreferences("language", MODE_PRIVATE);

        prefs.edit().putString("lan",lan).commit();


    }
    public String getLocale(){

        SharedPreferences prefs = context.getSharedPreferences("language", MODE_PRIVATE);


        String lan = prefs.getString("lan","ar");
        return lan;

    }
}
