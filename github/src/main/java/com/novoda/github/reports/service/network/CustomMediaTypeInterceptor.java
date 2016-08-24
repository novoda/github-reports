package com.novoda.github.reports.service.network;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

class CustomMediaTypeInterceptor implements Interceptor {

    private static final String ACCEPT_HEADER_KEY = "Accept";

    private static final String TIMELINE_API_MEDIA_TYPE = "application/vnd.github.mockingbird-preview";
    private static final String REACTIONS_API_MEDIA_TYPE = "application/vnd.github.squirrel-girl-preview";

    private final String customMediaType;

    /**
     * @see com.novoda.github.reports.service.timeline.TimelineService
     */
    @Deprecated
    static CustomMediaTypeInterceptor newInstanceForTimelineApi() {
        return new CustomMediaTypeInterceptor(TIMELINE_API_MEDIA_TYPE);
    }

    static CustomMediaTypeInterceptor newInstanceForReactionsApi() {
        return new CustomMediaTypeInterceptor(REACTIONS_API_MEDIA_TYPE);
    }

    CustomMediaTypeInterceptor(String customMediaType) {
        this.customMediaType = customMediaType;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = injectAcceptedCustomMediaTypeThrough(chain);
        return chain.proceed(request);
    }

    private Request injectAcceptedCustomMediaTypeThrough(Chain chain) {
        Request oldRequest = chain.request();
        return oldRequest.newBuilder()
                .addHeader(ACCEPT_HEADER_KEY, customMediaType)
                .build();
    }
}
