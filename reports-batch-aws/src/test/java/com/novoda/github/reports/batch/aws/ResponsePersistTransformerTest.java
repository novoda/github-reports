package com.novoda.github.reports.batch.aws;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ResponsePersistTransformerTest {

    private static final String ANY_HEADER_NAME = "AnyHeaderName";
    private static final String ANY_HEADER_VALUE = "any header value";
    private static final Headers ANY_HEADERS = Headers.of(ANY_HEADER_NAME, ANY_HEADER_VALUE);

    @Mock
    Observable.Transformer<Object, Object> persistTransformer;

    @InjectMocks
    ResponsePersistTransformer<Object> responsePersistTransformer;

    private Response<List<Object>> response;
    private Observable<Response<List<Object>>> inputObservable;
    private TestSubscriber<Response<List<Object>>> testSubscriber;

    @Before
    public void setUp() {
        initMocks(this);

        when(persistTransformer.call(any(Observable.class))).then(invocation -> invocation.getArgument(0));

        List<Object> elements = Collections.nCopies(10, new Object());
        response = Response.success(elements, ANY_HEADERS);
        inputObservable = Observable.just(response);

        testSubscriber = TestSubscriber.create();
    }

    @Test
    public void givenResponse_whenCompose_thenHandleOneResponseAndComplete() {

        whenCompose();

        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
    }

    @Test
    public void givenResponseWith10Elements_whenCompose_thenCallPersistTransformer10Times() {

        whenCompose();

        verify(persistTransformer, VerificationModeFactory.only()).call(any());
    }

    private void whenCompose() {
        Observable<Response<List<Object>>> actualObservable = inputObservable.compose(responsePersistTransformer);
        actualObservable.subscribe(testSubscriber);
    }

    @Test
    public void givenResponseWithHeaders_whenCompose_thenDoNotModifyHeaders() {

        whenCompose();

        Response<List<Object>> actual = testSubscriber.getOnNextEvents().get(0);
        assertEquals(response.headers().size(), actual.headers().size());
        assertEquals(response.headers().get(ANY_HEADER_NAME), actual.headers().get(ANY_HEADER_NAME));
        assertEquals(response.body(), actual.body());
    }

    @Test
    public void givenResponseWithHeaders_whenCompose_thenDoNotModifyBody() {

        whenCompose();

        Response<List<Object>> actual = testSubscriber.getOnNextEvents().get(0);
        assertEquals(response.body(), actual.body());
    }

}
