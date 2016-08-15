package com.novoda.floatschedule.network;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class RateLimitHandlerInterceptor implements Interceptor {

    private static final int HTTP_STATUS_CODE_TOO_MANY_REQUESTS = 429;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == HTTP_STATUS_CODE_TOO_MANY_REQUESTS) {
            throw new RateLimitEncounteredException("Rate limit encountered, Float supports up to 200 requests/minute.");
        }

        return response;
    }
}
