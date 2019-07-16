package com.example.refactoringwnamqos.measurments.webauthorizition;



import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitProvider {
    /*simple retrofit provider*/
    OkHttpClient client = new OkHttpClient();
    private final Retrofit retrofit;
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public RetrofitProvider(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://wnam-srv1.alel.net/")
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}