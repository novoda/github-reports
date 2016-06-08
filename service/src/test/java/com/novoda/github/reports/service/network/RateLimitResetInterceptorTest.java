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

public class RateLimitResetInterceptorTest {

    private static final String ANY_URL = "http://google.pt";
    private static final Protocol ANY_PROTOCOL = Protocol.HTTP_1_1;
    private static final int ANY_STATUS_CODE = 200;
    private static final long ANY_EPOCH_TIMESTAMP = 1464692786;
    private static final Request ANY_REQUEST = new Request.Builder().url(ANY_URL).build();

    @Mock
    RateLimitResetRepository mockLimitRepository;

    @Mock
    Interceptor.Chain mockChain;

    private TimeConverter timeConverter;

    private RateLimitResetInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        timeConverter = new TestTimeConverter();

        interceptor = new RateLimitResetInterceptor(mockLimitRepository, timeConverter);

        Mockito.when(mockChain.request()).thenReturn(ANY_REQUEST);

        Mockito.when(mockChain.proceed(Matchers.any(Request.class))).thenAnswer(
                (Answer<Response>) invocation ->
                        new Response.Builder()
                                .protocol(ANY_PROTOCOL)
                                .code(ANY_STATUS_CODE)
                                .request(ANY_REQUEST)
                                .header("X-RateLimit-Reset", String.valueOf(ANY_EPOCH_TIMESTAMP))
                                .build()
        );
    }

    @Test
    public void whenTheRequestIsIntercepted_thenTheTimestampIsStored() throws Exception {

        interceptor.intercept(mockChain);

        Mockito.verify(mockLimitRepository).setNextResetTime(Matchers.anyLong());
    }

    @Test
    public void whenTheRequestIsIntercepted_thenWeGetTheConvertedLimitResetTimestamp() throws Exception {

        Response response = interceptor.intercept(mockChain);
        long epochTimestamp = Long.parseLong(response.header("X-RateLimit-Reset"));

        long expected = timeConverter.toMillis(epochTimestamp);
        Mockito.verify(mockLimitRepository).setNextResetTime(expected);
    }

    private static class TestTimeConverter implements TimeConverter {
        @Override
        public long toMillis(long time) {
            return time * 1000L;
        }

        @Override
        public long toSeconds(long time) {
            return time / 1000L;
        }
    }

}
