package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.issue.RepositoryIssueEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EventPersisterTest {

    private static final Observable<RepositoryIssueEvent> ANY_OBSERVABLE = Observable.empty();

    @Mock
    PersistEventUserTransformer mockPersistEventUserTransformer;

    @Mock
    PersistEventTransformer mockPersistEventTransformer;

    private EventPersister eventPersister;

    @Before
    public void setUp() {
        initMocks(this);
        when(mockPersistEventUserTransformer.call(ANY_OBSERVABLE)).thenReturn(ANY_OBSERVABLE);
        when(mockPersistEventTransformer.call(ANY_OBSERVABLE)).thenReturn(ANY_OBSERVABLE);
        eventPersister = new EventPersister(mockPersistEventUserTransformer, mockPersistEventTransformer);
    }

    @Test
    public void givenAnObservable_whenComposing_thenTheEventPersistTransformerIsCalled() {

        eventPersister.call(ANY_OBSERVABLE);

        verify(mockPersistEventTransformer).call(ANY_OBSERVABLE);
    }

    @Test
    public void givenAnObservable_whenComposing_thenTheUserPersistTransformerIsCalled() {

        eventPersister.call(ANY_OBSERVABLE);

        verify(mockPersistEventUserTransformer).call(ANY_OBSERVABLE);
    }

}
