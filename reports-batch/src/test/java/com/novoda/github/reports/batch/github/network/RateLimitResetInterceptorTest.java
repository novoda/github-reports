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

public class RateLimitResetInterceptorTest {

    private static final String ANY_URL = "http://google.pt";
    private static final Protocol ANY_PROTOCOL = Protocol.HTTP_1_1;
    private static final int ANY_STATUS_CODE = 200;
    private static final String ANY_TIMESTAMP = String.valueOf(System.currentTimeMillis());
    private static final Request ANY_REQUEST = new Request.Builder().url(ANY_URL).build();

    @Mock
    RateLimitResetRepository mockLimitRepository;

    @Mock
    Interceptor.Chain mockChain;

    private RateLimitResetInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        interceptor = new RateLimitResetInterceptor(mockLimitRepository);

        when(mockChain.request()).thenReturn(ANY_REQUEST);

        when(mockChain.proceed(any(Request.class))).thenAnswer(
                (Answer<Response>) invocation ->
                        new Response.Builder()
                                .protocol(ANY_PROTOCOL)
                                .code(ANY_STATUS_CODE)
                                .request(ANY_REQUEST)
                                .header("X-RateLimit-Reset", ANY_TIMESTAMP)
                                .build()
        );
    }

    @Test
    public void whenTheRequestIsIntercepted_thenWeGetTheLimitResetTimestamp() throws Exception {

        Response response = interceptor.intercept(mockChain);
        long timestamp = Long.parseLong(response.header("X-RateLimit-Reset"));

        verify(mockLimitRepository).set(timestamp);
    }

}
