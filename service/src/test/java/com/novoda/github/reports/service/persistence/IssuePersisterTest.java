package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.issue.RepositoryIssue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IssuePersisterTest {

    private static final Observable<RepositoryIssue> ANY_OBSERVABLE = Observable.empty();

    @Mock
    PersistUserTransformer mockPersistUserTransformer;

    @Mock
    PersistIssueTransformer mockPersistIssueTransformer;

    private IssuePersister issuePersister;

    @Before
    public void setUp() {
        initMocks(this);
        when(mockPersistIssueTransformer.call(ANY_OBSERVABLE)).thenReturn(ANY_OBSERVABLE);
        when(mockPersistUserTransformer.call(ANY_OBSERVABLE)).thenReturn(ANY_OBSERVABLE);
        issuePersister = new IssuePersister(mockPersistUserTransformer, mockPersistIssueTransformer);
    }

    @Test
    public void givenAnObservable_whenComposing_thenTheIssuePersistTransformerIsCalled() {

        issuePersister.call(ANY_OBSERVABLE);

        verify(mockPersistIssueTransformer).call(ANY_OBSERVABLE);
    }

    @Test
    public void givenAnObservable_whenComposing_thenTheUserPersistTransformerIsCalled() {

        issuePersister.call(ANY_OBSERVABLE);

        verify(mockPersistUserTransformer).call(ANY_OBSERVABLE);
    }

}
