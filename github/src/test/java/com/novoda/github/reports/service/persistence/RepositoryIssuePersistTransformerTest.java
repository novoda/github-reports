package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.issue.RepositoryIssue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepositoryIssuePersistTransformerTest {

    private static final Observable<RepositoryIssue> ANY_OBSERVABLE = Observable.empty();

    @Mock
    PersistUserTransformer mockPersistUserTransformer;

    @Mock
    PersistIssueTransformer mockPersistIssueTransformer;

    private RepositoryIssuePersistTransformer repositoryIssuePersistTransformer;

    @Before
    public void setUp() {
        initMocks(this);
        when(mockPersistIssueTransformer.call(ANY_OBSERVABLE)).thenReturn(ANY_OBSERVABLE);
        when(mockPersistUserTransformer.call(ANY_OBSERVABLE)).thenReturn(ANY_OBSERVABLE);
        repositoryIssuePersistTransformer = new RepositoryIssuePersistTransformer(mockPersistUserTransformer, mockPersistIssueTransformer);
    }

    @Test
    public void givenAnObservable_whenComposing_thenTheIssuePersistTransformerIsCalled() {

        repositoryIssuePersistTransformer.call(ANY_OBSERVABLE);

        verify(mockPersistIssueTransformer).call(ANY_OBSERVABLE);
    }

    @Test
    public void givenAnObservable_whenComposing_thenTheUserPersistTransformerIsCalled() {

        repositoryIssuePersistTransformer.call(ANY_OBSERVABLE);

        verify(mockPersistUserTransformer).call(ANY_OBSERVABLE);
    }

}
