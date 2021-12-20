package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;

import java.io.File;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT=2000;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        ///// for dark modeee
//        File yourAppDir = new File(Environment.getExternalStorageDirectory()+File.separator+"memo");
//
//        if(!yourAppDir.exists() && !yourAppDir.isDirectory())
//        {
//            // create empty directory
//            if (yourAppDir.mkdirs())
//            {
//                Log.i("CreateDir","App dir created");
//            }
//            else
//            {
//                Log.w("CreateDir","Unable to create app dir!");
//            }
//        }
//        else
//        {
//            Log.i("CreateDir","App dir already exists");
//        }
        createDirectory("memo");
        createDirectory("memo/send");
        createDirectory("memo/recive");


        final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
        // The apps theme is decided depending upon the saved preferences on app startup
        String pref = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value));
        // Comparing to see which preference is selected and applying those theme settings
        if (pref.equals(darkModeValues[0]))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (pref.equals(darkModeValues[1]))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        classSharedPreferences = new ClassSharedPreferences(this);
        serverApi = new ServerApi(this);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if(classSharedPreferences.getUser()!=null){
                intent = new Intent(SplashScreen.this,
                        BasicActivity.class);
                    startActivity(intent);
                }
                else if (classSharedPreferences.getVerficationNumber()==null){
                //Intent is used to switch from one activity to another.
                intent  = new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(intent);
                        }
                else {
                    serverApi.register();

                }


                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
   void  createDirectory(String dName){
       File yourAppDir = new File(Environment.getExternalStorageDirectory()+File.separator+dName);

       if(!yourAppDir.exists() && !yourAppDir.isDirectory())
       {
           // create empty directory
           if (yourAppDir.mkdirs())
           {
               Log.i("CreateDir","App dir created");
           }
           else
           {
               Log.w("CreateDir","Unable to create app dir!");
           }
       }
       else
       {
           Log.i("CreateDir","App dir already exists");
       }


   }
    }
