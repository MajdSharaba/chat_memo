package com.yawar.chatmemo.Api;

import com.google.firebase.auth.UserInfo;
import com.google.gson.JsonObject;
import com.yawar.chatmemo.model.SignUpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface apiRest {
    @FormUrlEncoded // annotation used in POST type requests
    @POST("/retrofit/register.php")
        // API's endpoints
    Call<SignUpResponse> registration(@Field("name") String name,
                                      @Field("email") String email,
                                      @Field("password") String password,
                                      @Field("logintype") String logintype
                                      );

    @POST("signup.php")
    Call<String> createPost(@Field("phone") String phone);
}