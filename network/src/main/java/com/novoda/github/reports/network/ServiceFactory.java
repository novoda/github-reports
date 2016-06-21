package com.novoda.github.reports.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class ServiceFactory<S> {

    private final OkHttpClient okHttpClient;
    private final GsonConverterFactory gsonConverterFactory;
    private final RxJavaCallAdapterFactory rxJavaCallAdapterFactory;

    protected ServiceFactory(OkHttpClient okHttpClient,
                             GsonConverterFactory gsonConverterFactory,
                             RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

        this.okHttpClient = okHttpClient;
        this.gsonConverterFactory = gsonConverterFactory;
        this.rxJavaCallAdapterFactory = rxJavaCallAdapterFactory;
    }

    public S createService() {
        return createRetrofit()
                .create(getServiceClass());
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(getBaseEndpoint())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

    protected abstract Class<S> getServiceClass();

    protected abstract String getBaseEndpoint();
}
