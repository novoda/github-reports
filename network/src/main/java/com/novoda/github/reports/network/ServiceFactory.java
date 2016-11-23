package com.novoda.github.reports.network;

import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class ServiceFactory<S> {

    private final OkHttpClient okHttpClient;
    private final GsonConverterFactory gsonConverterFactory;
    private final List<CallAdapter.Factory> callAdapterFactories;

    protected ServiceFactory(OkHttpClient okHttpClient,
                             GsonConverterFactory gsonConverterFactory,
                             CallAdapter.Factory... callAdapterFactories) {

        this.okHttpClient = okHttpClient;
        this.gsonConverterFactory = gsonConverterFactory;
        this.callAdapterFactories = Arrays.asList(callAdapterFactories);
    }

    public S createService() {
        return createRetrofit()
                .create(getServiceClass());
    }

    private Retrofit createRetrofit() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(getBaseEndpoint())
                .addConverterFactory(gsonConverterFactory);

        callAdapterFactories.forEach(builder::addCallAdapterFactory);

        return builder.build();
    }

    protected abstract Class<S> getServiceClass();

    protected abstract String getBaseEndpoint();
}
