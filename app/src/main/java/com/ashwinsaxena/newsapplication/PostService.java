package com.ashwinsaxena.newsapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PostService {
    String BASE_URL = "https://newsapi.org/v2/";

    @GET("top-headlines")
    Call<NewsStructure> getNews(
            @Query("country") String country,
            @Query("pageSize") int pageSize,
            @Query("apiKey") String key
    );
}
