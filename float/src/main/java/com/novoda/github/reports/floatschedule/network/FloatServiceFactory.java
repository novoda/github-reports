package com.novoda.github.reports.floatschedule.network;

import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FloatServiceFactory {

    private static final String FLOAT_ENDPOINT = "https://api.float.com/api/v1/";

    private final OkHttpClient okHttpClient;
    private final GsonConverterFactory gsonConverterFactory;
    private final RxJavaCallAdapterFactory rxJavaCallAdapterFactory;

    public FloatServiceFactory(OkHttpClient okHttpClient,
                               GsonConverterFactory gsonConverterFactory,
                               RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

        this.okHttpClient = okHttpClient;
        this.gsonConverterFactory = gsonConverterFactory;
        this.rxJavaCallAdapterFactory = rxJavaCallAdapterFactory;
    }

}
