package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.repository.Repository;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PersistRepositoryTransformerTest {

    @Mock
    Observable.Operator<List<Repository>, List<Repository>> operator;

    @InjectMocks
    PersistRepositoryTransformer transformer;

    private TestSubscriber<Repository> testSubscriber;

    private static final Repository ANY_REPOSITORY = new Repository();

    private static final List<Repository> repositoryList = Arrays.asList(ANY_REPOSITORY, ANY_REPOSITORY);

    private static final Observable<Repository> ANY_OBSERVABLE = Observable.from(repositoryList);

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        testSubscriber = TestSubscriber.create();
    }

    @Test
    public void givenAnyObservable_whenCompose_thenDoNotAlterValues() {
        when(operator.call(any())).thenAnswer(invocation -> createFlowSubscriber(invocation.getArgument(0)));

        ANY_OBSERVABLE.compose(transformer).subscribe(testSubscriber);

        testSubscriber.assertValues(repositoryList.toArray(new Repository[0]));
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
