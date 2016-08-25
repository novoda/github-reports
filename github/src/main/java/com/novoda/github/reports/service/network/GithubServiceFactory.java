package com.novoda.github.reports.service.network;

import com.novoda.github.reports.network.HttpClientFactory;
import com.novoda.github.reports.network.Interceptors;
import com.novoda.github.reports.network.OkHttpClientFactory;
import com.novoda.github.reports.network.ServiceFactory;

import com.novoda.github.reports.service.properties.GithubCredentialsReader;
import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class GithubServiceFactory extends ServiceFactory<GithubApiService> {

    private static final String GITHUB_ENDPOINT = "https://api.github.com/";

    public static GithubServiceFactory newInstance() {
        Interceptors githubInterceptors = GithubInterceptors.defaultInterceptors();
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newInstance(githubInterceptors);
        return newInstance(httpClientFactory);
    }

    public static GithubServiceFactory newInstance(GithubCredentialsReader githubCredentialsReader) {
        Interceptors githubInterceptors = GithubInterceptors.defaultInterceptors(githubCredentialsReader);
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newInstance(githubInterceptors);
        return newInstance(httpClientFactory);
    }

    public static GithubServiceFactory newCachingInstance() {
        Interceptors githubInterceptors = GithubInterceptors.defaultInterceptors();
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newCachingInstance(githubInterceptors);
        return newInstance(httpClientFactory);
    }

    private static GithubServiceFactory newInstance(HttpClientFactory httpClientFactory) {
        OkHttpClient okHttpClient = httpClientFactory.createClient();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
        return new GithubServiceFactory(okHttpClient, gsonConverterFactory, rxJavaCallAdapterFactory);
    }

    private GithubServiceFactory(OkHttpClient okHttpClient,
                                 GsonConverterFactory gsonConverterFactory,
                                 RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

        super(okHttpClient, gsonConverterFactory, rxJavaCallAdapterFactory);
    }

    @Override
    protected Class<GithubApiService> getServiceClass() {
        return GithubApiService.class;
    }

    @Override
    protected String getBaseEndpoint() {
        return GITHUB_ENDPOINT;
    }

}
