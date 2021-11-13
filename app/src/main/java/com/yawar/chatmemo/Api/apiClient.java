package com.yawar.chatmemo.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class apiClient {

    //base url
//    public static final String BASE_URL = "http://192.168.43.254/larntech/api/";
    public static String BASE_URL = "http://192.168.1.9:8080/yawar_chat/APIS/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
