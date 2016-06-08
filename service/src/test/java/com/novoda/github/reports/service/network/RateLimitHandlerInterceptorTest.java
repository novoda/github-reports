package com.novoda.github.reports.service.network;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static org.hamcrest.CoreMatchers.isA;

public class RateLimitHandlerInterceptorTest {

    private static final int VALID_COUNTER = 5000;
    private static final int ZERO_COUNTER = 0;

    private static final String ANY_URL = "http://google.pt";
    private static final Protocol ANY_PROTOCOL = Protocol.HTTP_1_1;
    private static final Request ANY_REQUEST = new Request.Builder().url(ANY_URL).build();

    private Response ANY_SUCCESSFUL_RESPONSE;
    private Response ANY_RATE_LIMIT_RESPONSE;
    private Response ANY_OTHER_ERROR_RESPONSE;

    @Mock
    RateLimitRemainingCounter mockRateLimitRemainingCounter;

    @Mock
    RateLimitResetRepository mockRateLimitResetRepository;

    @Mock
    Interceptor.Chain mockChain;

    @InjectMocks
    RateLimitHandlerInterceptor interceptor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(mockChain.request()).thenReturn(ANY_REQUEST);

        ANY_SUCCESSFUL_RESPONSE = buildAnyResponseWithCode(200);
        ANY_RATE_LIMIT_RESPONSE = buildAnyResponseWithCode(403);
        ANY_OTHER_ERROR_RESPONSE = buildAnyResponseWithCode(500);
    }

    private Response buildAnyResponseWithCode(int code) {
        return new Response.Builder()
                .code(code)
                .protocol(ANY_PROTOCOL)
                .request(ANY_REQUEST)
                .build();
    }

    @Test
    public void givenSuccessfulCall_whenIntercept_thenReturnResponse() throws IOException {
        Mockito.when(mockChain.proceed(ANY_REQUEST)).thenReturn(ANY_SUCCESSFUL_RESPONSE);
        Mockito.when(mockRateLimitRemainingCounter.get()).thenReturn(VALID_COUNTER);

        Response response = interceptor.intercept(mockChain);

        Assert.assertEquals(ANY_SUCCESSFUL_RESPONSE, response);
    }

    @Test
    public void givenRateLimitCallAndZeroCounter_whenIntercept_thenThrowIoException() throws IOException {
        Mockito.when(mockChain.proceed(ANY_REQUEST)).thenReturn(ANY_RATE_LIMIT_RESPONSE);
        Mockito.when(mockRateLimitRemainingCounter.get()).thenReturn(ZERO_COUNTER);

        thrown.expect(IOException.class);
        thrown.expectCause(isA(RateLimitEncounteredException.class));

        interceptor.intercept(mockChain);
    }

    @Test
    public void givenRateLimitCallAndValidCounter_whenIntercept_thenDoNotThrowIoException() throws IOException {
        Mockito.when(mockChain.proceed(ANY_REQUEST)).thenReturn(ANY_RATE_LIMIT_RESPONSE);
        Mockito.when(mockRateLimitRemainingCounter.get()).thenReturn(VALID_COUNTER);

        Response response = interceptor.intercept(mockChain);

        Assert.assertEquals(ANY_RATE_LIMIT_RESPONSE, response);
    }

    @Test
    public void givenErroringCall_whenIntercept_thenDoNotHandle() throws IOException {
        Mockito.when(mockChain.proceed(ANY_REQUEST)).thenReturn(ANY_OTHER_ERROR_RESPONSE);
        Mockito.when(mockRateLimitRemainingCounter.get()).thenReturn(VALID_COUNTER);

        Response response = interceptor.intercept(mockChain);

        Assert.assertEquals(ANY_OTHER_ERROR_RESPONSE, response);
    }

}
