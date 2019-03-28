package com.example.cashnoteapp.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConfig {


    public static ApiService getApiService(){

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.97.37/cashnote/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService service = retrofit.create(ApiService.class);
        return service;
    }
}
