package com.novoda.github.reports.service.network;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class RateLimitCountInterceptorTest {

    private static final String ANY_URL = "http://google.pt";
    private static final Protocol ANY_PROTOCOL = Protocol.HTTP_1_1;
    private static final int ANY_STATUS_CODE = 200;
    private static final String ANY_COUNT = "5";
    private static final Request ANY_REQUEST = new Request.Builder().url(ANY_URL).build();

    @Mock
    RateLimitRemainingCounter mockCounter;

    @Mock
    Interceptor.Chain mockChain;

    private RateLimitCountInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        interceptor = new RateLimitCountInterceptor(mockCounter);

        Mockito.when(mockChain.request()).thenReturn(ANY_REQUEST);

        Mockito.when(mockChain.proceed(Matchers.any(Request.class))).thenAnswer(
                (Answer<Response>) invocation ->
                        new Response.Builder()
                                .protocol(ANY_PROTOCOL)
                                .code(ANY_STATUS_CODE)
                                .request(ANY_REQUEST)
                                .header("X-RateLimit-Remaining", ANY_COUNT)
                                .build()
        );
    }

    @Test
    public void whenTheRequestIsIntercepted_thenWeGetTheRemainingRateLimit() throws Exception {

        Response response = interceptor.intercept(mockChain);
        int count = Integer.parseInt(response.header("X-RateLimit-Remaining"));

        Mockito.verify(mockCounter).set(count);
    }
}
