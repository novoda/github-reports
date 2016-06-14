package com.novoda.github.reports.service.network;

import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

class Interceptors {

    private List<Interceptor> interceptors;

    static Interceptors defaultInterceptors() {
        GithubCredentialsReader githubCredentialsReader = GithubCredentialsReader.newInstance();
        String token = githubCredentialsReader.getAuthToken();
        return new Interceptors()
                .withOAuthTokenInterceptor(token)
                .withRateLimitHandlerInterceptor()
                .withRateLimitCountInterceptor()
                .withRateLimitResetInterceptor()
                .withCustomMediaTypeInterceptor();
    }

    private Interceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    private Interceptors() {
        this(new ArrayList<>());
    }

    Interceptors with(Interceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    Interceptors withOAuthTokenInterceptor(String token) {
        return with(new OAuthTokenInterceptor(token));
    }

    Interceptors withRateLimitHandlerInterceptor() {
        return with(RateLimitHandlerInterceptor.newInstance());
    }

    Interceptors withRateLimitCountInterceptor() {
        return with(RateLimitCountInterceptor.newInstance());
    }

    Interceptors withRateLimitResetInterceptor() {
        return with(RateLimitResetInterceptor.newInstance());
    }

    Interceptors withCustomMediaTypeInterceptor() {
        return with(CustomMediaTypeInterceptor.newInstanceForTimelineApi());
    }

    Interceptors withDebugInterceptor() {
        return with(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
    }

    List<Interceptor> asList() {
        return interceptors;
    }

    Stream<Interceptor> stream() {
        return interceptors.stream();
    }
}
