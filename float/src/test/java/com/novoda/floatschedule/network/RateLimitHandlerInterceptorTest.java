package com.novoda.floatschedule.network;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RateLimitHandlerInterceptorTest {

    private static final int RATE_LIMIT_STATUS_CODE = 429;

    private RateLimitHandlerInterceptor interceptor = new RateLimitHandlerInterceptor();

    @Mock
    Interceptor.Chain mockChain;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        Request oldRequest = new Request.Builder().url("https://api.float.com/api/v1/some-endpoint").build();
        when(mockChain.request()).thenReturn(oldRequest);

        when(mockChain.proceed(any(Request.class))).thenAnswer(
                invocation -> {
                    Request request = (Request) invocation.getArguments()[0];
                    return new Response.Builder()
                            .protocol(Protocol.HTTP_1_1)
                            .code(RATE_LIMIT_STATUS_CODE)
                            .request(request)
                            .build();
                });
    }

    @Test
    public void whenTheRequestIsIntercepted_thenTheRateLimitErrorIsThrown() throws Exception {
        expectedException.expect(RateLimitEncounteredException.class);

        interceptor.intercept(mockChain);
    }
}
