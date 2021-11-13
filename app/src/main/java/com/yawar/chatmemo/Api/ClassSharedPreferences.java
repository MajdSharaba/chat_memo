package com.yawar.chatmemo.Api;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.yawar.chatmemo.model.UserModel;

import static android.content.Context.MODE_PRIVATE;

public class ClassSharedPreferences {
    Context context;

    public ClassSharedPreferences(Context context) {
        this.context = context;
    }


    public void setUser( UserModel user){
        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("UserModel", json).commit();}

    public UserModel getUser( ){
        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString("UserModel", "");
        UserModel user = gson.fromJson(json, UserModel.class);
        return  user;

    }
    public void setName(String name){
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);
        prefs.edit().putString("name",name).commit();
        System.out.println("Memo+"+name);

    }
    public void setVerficationNumber(String number){
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);
        prefs.edit().putString("verficationNumber",number).commit();

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
    public String getVerficationNumber(){
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);

        String number = prefs.getString("verficationNumber",null);
        return  number;

    }
    public String getNumber(){
        SharedPreferences prefs = context.getSharedPreferences("profile", MODE_PRIVATE);

        String number = prefs.getString("number","UserName");
        return  number;

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







//        prefs.edit().p("user",user).commit();



}
