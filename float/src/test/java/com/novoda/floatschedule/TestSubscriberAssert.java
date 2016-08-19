package com.novoda.floatschedule;

import rx.Observable;
import rx.functions.Action1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

class TestSubscriberAssert<T> {

    private final Observable<T> observable;
    private TestSubscriber<T> testSubscriber;

    static <T> TestSubscriberAssert<T> assertThatAnObservable(Observable<T> observable) {
        return new TestSubscriberAssert<>(observable);
    }

    private TestSubscriberAssert(Observable<T> observable) {
        this.observable = observable;
    }

    TestSubscriberAssert<T> hasEmittedValues(TestSubscriberCustomAssert<T> customAssert, T... values) {
        TestSubscriber<T> testSubscriber = subscribeMaybe(this.observable);

        List<T> actualElements = testSubscriber.getOnNextEvents();
        List<T> expectedElements = asList(values);

        assertEquals(expectedElements.size(), actualElements.size());
        for (int i = 0; i < expectedElements.size(); i++) {
            customAssert.assertThat(expectedElements.get(i), actualElements.get(i));
        }

        return this;
    }

    TestSubscriberAssert<T> hasEmittedValues(Action1<T> customAssert) {
        TestSubscriber<T> testSubscriber = subscribeMaybe(this.observable);

        testSubscriber.getOnNextEvents().forEach(customAssert::call);

        return this;
    }

    TestSubscriberAssert<T> hasEmittedValues(T... values) {
        TestSubscriber<T> testSubscriber = subscribeMaybe(this.observable);

        testSubscriber.assertReceivedOnNext(asList(values));

        return this;
    }

    TestSubscriberAssert<T> hasEmittedNoValues() {
        TestSubscriber<T> testSubscriber = subscribeMaybe(this.observable);

        testSubscriber.assertNoValues();

        return this;
    }

    private TestSubscriber<T> subscribeMaybe(Observable<T> observable) {
        if (this.testSubscriber == null) {
            this.testSubscriber = TestSubscriber.create();
            observable
                    .subscribeOn(Schedulers.immediate())
                    .subscribe(this.testSubscriber);
        }

        return this.testSubscriber;
    }

    TestSubscriberAssert<T> hasThrown(Class<? extends Exception> exceptionClass) {
        TestSubscriber<T> testSubscriber = subscribeMaybe(this.observable);

        testSubscriber.assertError(exceptionClass);

        return this;
    }


    @FunctionalInterface
    interface TestSubscriberCustomAssert<T> {
        void assertThat(T expected, T actual);
    }
}
