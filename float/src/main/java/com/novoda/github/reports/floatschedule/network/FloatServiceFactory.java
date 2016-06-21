package com.novoda.github.reports.floatschedule.network;

import com.novoda.github.reports.network.ServiceFactory;

import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FloatServiceFactory extends ServiceFactory<FloatApiService> {

    private static final String FLOAT_ENDPOINT = "https://api.float.com/api/v1/";

    private FloatServiceFactory(OkHttpClient okHttpClient,
                                GsonConverterFactory gsonConverterFactory,
                                RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

        super(okHttpClient, gsonConverterFactory, rxJavaCallAdapterFactory);
    }

    @Override
    protected Class<FloatApiService> getServiceClass() {
        return FloatApiService.class;
    }

    @Override
    protected String getBaseEndpoint() {
        return FLOAT_ENDPOINT;
    }

}
