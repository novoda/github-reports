package com.novoda.github.reports.service.network;

import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import okhttp3.logging.HttpLoggingInterceptor;

class GithubInterceptors extends Interceptors {

    static Interceptors defaultInterceptors() {
        GithubCredentialsReader githubCredentialsReader = GithubCredentialsReader.newInstance();
        String token = githubCredentialsReader.getAuthToken();
        return new GithubInterceptors()
                .withOAuthTokenInterceptor(token)
                .withRateLimitHandlerInterceptor()
                .withRateLimitCountInterceptor()
                .withRateLimitResetInterceptor()
                .withCustomMediaTypeInterceptor();
    }

    GithubInterceptors withOAuthTokenInterceptor(String token) {
        return (GithubInterceptors) with(new OAuthTokenInterceptor(token));
    }

    GithubInterceptors withRateLimitHandlerInterceptor() {
        return (GithubInterceptors) with(RateLimitHandlerInterceptor.newInstance());
    }

    GithubInterceptors withRateLimitCountInterceptor() {
        return (GithubInterceptors) with(RateLimitCountInterceptor.newInstance());
    }

    GithubInterceptors withRateLimitResetInterceptor() {
        return (GithubInterceptors) with(RateLimitResetInterceptor.newInstance());
    }

    GithubInterceptors withCustomMediaTypeInterceptor() {
        return (GithubInterceptors) with(CustomMediaTypeInterceptor.newInstanceForTimelineApi());
    }

    GithubInterceptors withDebugInterceptor() {
        return (GithubInterceptors) with(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
    }

}
