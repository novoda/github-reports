package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.network.HttpClientFactory;
import com.novoda.github.reports.network.OkHttpClientFactory;
import com.novoda.github.reports.network.ServiceFactory;

import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SheetsServiceFactory extends ServiceFactory<SheetsApiService> {

    // @RUI this is going to be an issue 'cause we don't just append on the endpoint
    // https://spreadsheets.google.com/feeds/list/1rMeGnlugO312to0loBwN3x0QTvAxoHwv4Pe_SYXR1YE/1/public/basic?alt=json
    // https://spreadsheets.google.com/feeds/list/{ID}/1/public/basic?alt=json
    private static final String ENDPOINT = "https://spreadsheets.google.com/feeds/list/";

    public static SheetsServiceFactory newInstance() {
        //Interceptors floatInterceptors = FloatInterceptors.defaultInterceptors();
        //HttpClientFactory httpClientFactory = OkHttpClientFactory.newInstance(floatInterceptors);
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newInstance();
        return newInstance(httpClientFactory);
    }

    public static SheetsServiceFactory newCachingInstance() {
        //Interceptors floatInterceptors = FloatInterceptors.defaultInterceptors();
        //HttpClientFactory httpClientFactory = OkHttpClientFactory.newCachingInstance(floatInterceptors);
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
