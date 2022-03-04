package com.ashwinsaxena.newsapplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static PostService getApiInterface() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(PostService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }

        return retrofit.create(PostService.class);
    }

}
