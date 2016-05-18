package com.novoda.github.reports.github.network;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RateLimitCountInterceptorTest {

    private static final String ANY_URL = "http://google.pt";
    private static final Protocol ANY_PROTOCOL = Protocol.HTTP_1_1;
    private static final int ANY_STATUS_CODE = 200;
    private static final String ANY_COUNT = "5";

    @Mock
    RateLimitRemainingCounter mockCounter;

    @Mock
    Interceptor.Chain mockChain;

    private RateLimitCountInterceptor interceptor;
    private Request ANY_REQUEST = new Request.Builder().url(ANY_URL).build();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        interceptor = new RateLimitCountInterceptor(mockCounter);

        when(mockChain.request()).thenReturn(ANY_REQUEST);

        when(mockChain.proceed(any(Request.class))).thenAnswer(
                (Answer<Response>) invocation ->
                        new Response.Builder()
                                .protocol(ANY_PROTOCOL)
                                .code(ANY_STATUS_CODE)
                                .request(ANY_REQUEST)
                                .header(RateLimitCountInterceptor.REMAINING_RATE_LIMIT_HEADER, ANY_COUNT)
                                .build()
        );
    }

    @Test
    public void whenTheRequestIsIntercepted_thenWeGetTheRemainingRateLimit() throws Exception {

        Response response = interceptor.intercept(mockChain);
        int count = Integer.parseInt(response.header(RateLimitCountInterceptor.REMAINING_RATE_LIMIT_HEADER));

        verify(mockCounter).set(count);
    }
}
