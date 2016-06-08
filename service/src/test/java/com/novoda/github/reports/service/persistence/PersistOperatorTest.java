package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.ConverterException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;

import rx.Observable;
import rx.observers.TestSubscriber;

public class PersistOperatorTest {

    @Mock
    DataLayer<Object> dataLayer;

    @Mock
    Converter<Object, Object> converter;

    @InjectMocks
    PersistOperator<Object, Object> operator;

    private static final Object ANY_OBJECT = new Object();

    private static final List<List<Object>> objectLists = Arrays.asList(
            Arrays.asList(ANY_OBJECT, ANY_OBJECT),
            Collections.singletonList(ANY_OBJECT),
            Arrays.asList(ANY_OBJECT, ANY_OBJECT, ANY_OBJECT)
    );

    private static final Observable<List<Object>> ANY_OBSERVABLE = Observable.from(objectLists);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenAnyObservable_whenLift_thenConvert() throws ConverterException {
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe();

        Mockito.verify(converter, VerificationModeFactory.times(3)).convertListFrom(anyListOfModelObject());
    }

    @Test
    public void givenAnyObservable_whenLift_thenPersist() throws DataLayerException {
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe();

        Mockito.verify(dataLayer, VerificationModeFactory.times(3)).updateOrInsert(anyListOfModelObject());
    }

    @Test
    public void givenAnyCompletedObservable_whenLift_thenComplete() throws DataLayerException {
        TestSubscriber<List<Object>> testSubscriber = new TestSubscriber<>();
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
    }

    @Test
    public void givenAnyObservable_whenLift_thenEmitSameItems() throws DataLayerException {
        TestSubscriber<List<Object>> testSubscriber = new TestSubscriber<>();
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe(testSubscriber);

        testSubscriber.assertValues((List<Object>[]) objectLists.toArray());
        testSubscriber.assertCompleted();
    }

    @Test
    public void givenAnyObservableAndFailingPersist_whenLift_thenEmitError() throws DataLayerException {
        Mockito.when(dataLayer.updateOrInsert(anyListOfModelObject())).thenThrow(DataLayerException.class);

        TestSubscriber<List<Object>> testSubscriber = new TestSubscriber<>();
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe(testSubscriber);

        testSubscriber.assertError(DataLayerException.class);
        testSubscriber.assertNotCompleted();
    }

    private static List<Object> anyListOfModelObject() {
        return Matchers.anyListOf(Object.class);
    }

}
