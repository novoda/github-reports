package com.novoda.github.reports.service.network;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RateLimitCountInterceptorTest {

    private static final String ANY_URL = "http://google.pt";
    private static final Protocol ANY_PROTOCOL = Protocol.HTTP_1_1;
    private static final int ANY_STATUS_CODE = 200;
    private static final String ANY_COUNT = "5";
    private static final String EMPTY_COUNT = "";
    private static final Request ANY_REQUEST = new Request.Builder().url(ANY_URL).build();

    @Mock
    RateLimitRemainingCounter mockCounter;

    @Mock
    Interceptor.Chain mockChain;

    private RateLimitCountInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        interceptor = new RateLimitCountInterceptor(mockCounter);

        when(mockChain.request()).thenReturn(ANY_REQUEST);
    }

    @Test
    public void givenMissingRateLimitRemaining_whenTheRequestIsIntercepted_thenCounterIsSetToZero() throws Exception {
        givenChainResponseWithoutRateLimitRemaining();

        interceptor.intercept(mockChain);

        verify(mockCounter).set(0);
    }

    @Test
    public void givenEmptyRateLimitRemaining_whenTheRequestIsIntercepted_thenCounterIsSetToZero() throws Exception {
        givenChainResponseWithRateLimitRemainingOf(EMPTY_COUNT);

        interceptor.intercept(mockChain);

        verify(mockCounter).set(0);
    }

    @Test
    public void givenARateLimitRemaining_whenTheRequestIsIntercepted_thenWeGetTheRemainingRateLimit() throws Exception {
        givenChainResponseWithRateLimitRemainingOf(ANY_COUNT);

        Response response = interceptor.intercept(mockChain);

        assertThat(response.header("X-RateLimit-Remaining")).isEqualTo(ANY_COUNT);
    }

    @Test
    public void givenARateLimitRemaining_whenTheRequestIsIntercepted_thenTheCounterIsSet() throws Exception {
        givenChainResponseWithRateLimitRemainingOf(ANY_COUNT);

        interceptor.intercept(mockChain);

        verify(mockCounter).set(Integer.parseInt(ANY_COUNT));
    }

    private void givenChainResponseWithRateLimitRemainingOf(String count) throws IOException {
        when(mockChain.proceed(any(Request.class))).thenAnswer(
                invocation -> new Response.Builder()
                        .protocol(ANY_PROTOCOL)
                        .code(ANY_STATUS_CODE)
                        .request(ANY_REQUEST)
                        .header("X-RateLimit-Remaining", count)
                        .build()
        );
    }

    private void givenChainResponseWithoutRateLimitRemaining() throws IOException {
        when(mockChain.proceed(any(Request.class))).thenAnswer(
                invocation -> new Response.Builder()
                        .protocol(ANY_PROTOCOL)
                        .code(ANY_STATUS_CODE)
                        .request(ANY_REQUEST)
                        .build()
        );
    }
}
