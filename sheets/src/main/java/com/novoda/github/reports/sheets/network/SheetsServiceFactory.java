package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.network.HttpClientFactory;
import com.novoda.github.reports.network.OkHttpClientFactory;
import com.novoda.github.reports.network.ServiceFactory;

import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SheetsServiceFactory extends ServiceFactory<SheetsApiService> {

    private static final String ENDPOINT = "https://spreadsheets.google.com/feeds/list/";

    public static SheetsServiceFactory newInstance() {
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newInstance();
        return newInstance(httpClientFactory);
    }

    public static SheetsServiceFactory newCachingInstance() {
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newCachingInstance();
        return newInstance(httpClientFactory);
    }

    private static SheetsServiceFactory newInstance(HttpClientFactory httpClientFactory) {
        OkHttpClient okHttpClient = httpClientFactory.createClient();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
        return new SheetsServiceFactory(okHttpClient, gsonConverterFactory, rxJavaCallAdapterFactory);
    }

    private SheetsServiceFactory(OkHttpClient okHttpClient,
                                 GsonConverterFactory gsonConverterFactory,
                                 RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

        super(okHttpClient, gsonConverterFactory, rxJavaCallAdapterFactory);
    }

    @Override
    protected Class<SheetsApiService> getServiceClass() {
        return SheetsApiService.class;
    }

    @Override
    protected String getBaseEndpoint() {
        return ENDPOINT;
    }

}
