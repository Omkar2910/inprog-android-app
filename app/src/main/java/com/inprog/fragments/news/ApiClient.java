package com.inprog.fragments.news;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://newsapi.org/v2/";
    private static com.inprog.fragments.news.ApiClient apiClient;
    private static Retrofit retrofit;

    private ApiClient() {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized com.inprog.fragments.news.ApiClient getInstance() {
        if (apiClient == null) {
            apiClient = new com.inprog.fragments.news.ApiClient();
        }
        return apiClient;
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }
}