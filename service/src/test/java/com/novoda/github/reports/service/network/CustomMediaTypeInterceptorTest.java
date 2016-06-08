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

import static org.junit.Assert.assertEquals;

public class CustomMediaTypeInterceptorTest {

    private static final String ANY_TYPE = "tipo";
    private static final String ANY_URL = "http://www.google.pt";
    private static final Protocol ANY_PROTOCOL = Protocol.HTTP_1_1;
    private static final int ANY_STATUS_CODE = 200;

    private Interceptor interceptor = new CustomMediaTypeInterceptor(ANY_TYPE);

    @Mock
    Interceptor.Chain mockChain;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Request oldRequest = new Request.Builder().url(ANY_URL).build();
        Mockito.when(mockChain.request()).thenReturn(oldRequest);

        Mockito.when(mockChain.proceed(Matchers.any(Request.class))).thenAnswer(
                (Answer<Response>) invocation -> {
                    Request request = (Request) invocation.getArguments()[0];
                    return new Response.Builder()
                            .protocol(ANY_PROTOCOL)
                            .code(ANY_STATUS_CODE)
                            .request(request)
                            .build();
                });
    }

    @Test
    public void whenTheRequestIsIntercepted_thenTheCustomMediaTypeIsInjected() throws Exception {

        Response response = interceptor.intercept(mockChain);

        String actual = response.request().headers().get("Accept");
        assertEquals(ANY_TYPE, actual);
    }

}
