package com.novoda.github.reports.batch.persistence;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PersistTransformerTest {

    private static final int ANY_BUFFER_SIZE = 10;
    
    @Mock
    PersistOperator<Object, Object> operator;

    PersistTransformer<Object, Object> transformer;

    private TestSubscriber<Object> testSubscriber;

    private static final Object ANY_OBJECT = new Object();

    private static final List<Object> objectList = Arrays.asList(ANY_OBJECT, ANY_OBJECT);

    private static final Observable<Object> ANY_OBSERVABLE = Observable.from(objectList);

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        transformer = new PersistTransformer<>(operator, ANY_BUFFER_SIZE);
        testSubscriber = TestSubscriber.create();
    }

    @Test
    public void givenAnyObservable_whenCompose_thenDoNotAlterValues() {
        when(operator.call(any())).thenAnswer(invocation -> createFlowSubscriber(invocation.getArgument(0)));

        ANY_OBSERVABLE.compose(transformer).subscribe(testSubscriber);

        testSubscriber.assertValues(objectList.toArray(new Object[0]));
        testSubscriber.assertCompleted();
    }

    private <T> Subscriber<T> createFlowSubscriber(Subscriber<T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(T t) {
                subscriber.onNext(t);
            }
        };
    }

    @Test
    public void givenInvalidObservableForTransformer_whenCompose_thenEmitError() {
        when(operator.call(any())).thenAnswer(invocation -> createErroringSubscriber(invocation.getArgument(0)));

        ANY_OBSERVABLE.compose(transformer).subscribe(testSubscriber);

        testSubscriber.assertNotCompleted();
        testSubscriber.assertError(Exception.class);
    }

    private <T> Subscriber<T> createErroringSubscriber(Subscriber<T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(T t) {
                subscriber.onError(new Exception());
            }
        };
    }

}
