package com.yawar.chatmemo.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.yawar.chatmemo.Api.ClassSharedPreferences;
import com.yawar.chatmemo.R;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=2000;
    ClassSharedPreferences classSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        classSharedPreferences = new ClassSharedPreferences(this);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if(classSharedPreferences.getUser()!=null){
                intent = new Intent(SplashScreen.this,
                        BasicActivity.class);}
                else if (classSharedPreferences.getVerficationNumber()==null){
                //Intent is used to switch from one activity to another.
                intent  = new Intent(SplashScreen.this,LoginActivity.class);
                        }
                else {
                    intent  = new Intent(SplashScreen.this, RegisterActivity.class);
                }

                startActivity(intent);
                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
    }
