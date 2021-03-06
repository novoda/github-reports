package com.novoda.github.reports.sheets.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.novoda.github.reports.network.HttpClientFactory;
import com.novoda.github.reports.network.OkHttpClientFactory;
import com.novoda.github.reports.network.ServiceFactory;
import com.novoda.github.reports.sheets.convert.EntryDeserializer;
import com.novoda.github.reports.sheets.sheet.Entry;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
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

        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<Entry> jsonDeserializer = new EntryDeserializer();
        gsonBuilder.registerTypeAdapter(Entry.class, jsonDeserializer);
        Gson gson = gsonBuilder.create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        CallAdapter.Factory entryAdapterFactory = new EntryCallAdapterFactory();
        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

        return new SheetsServiceFactory(okHttpClient, gsonConverterFactory, entryAdapterFactory, rxJavaCallAdapterFactory);
    }

    private SheetsServiceFactory(OkHttpClient okHttpClient,
                                 GsonConverterFactory gsonConverterFactory,
                                 CallAdapter.Factory factory,
                                 RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

        super(okHttpClient, gsonConverterFactory, factory, rxJavaCallAdapterFactory);
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
