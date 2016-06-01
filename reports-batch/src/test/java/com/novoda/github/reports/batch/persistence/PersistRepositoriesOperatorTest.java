package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PersistRepositoriesOperatorTest {

    @Mock
    RepoDataLayer repoDataLayer;

    @Mock
    Converter<Repository, com.novoda.github.reports.data.model.Repository> converter;

    @InjectMocks
    PersistRepositoriesOperator operator;

    private static final Repository ANY_REPOSITORY = new Repository();

    private static final List<List<Repository>> repositoryLists = Arrays.asList(
            Arrays.asList(ANY_REPOSITORY, ANY_REPOSITORY),
            Collections.singletonList(ANY_REPOSITORY),
            Arrays.asList(ANY_REPOSITORY, ANY_REPOSITORY, ANY_REPOSITORY)
    );

    private static final Observable<List<Repository>> ANY_OBSERVABLE = Observable.from(repositoryLists);

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAnyObservable_whenLift_thenConvert() {
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe();

        verify(converter, VerificationModeFactory.times(3)).convertListFrom(anyListOf(Repository.class));
    }

    @Test
    public void givenAnyObservable_whenLift_thenPersist() throws DataLayerException {
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe();

        verify(repoDataLayer, VerificationModeFactory.times(3)).updateOrInsert(anyListOfModelRepository());
    }

    @Test
    public void givenAnyCompletedObservable_whenLift_thenComplete() throws DataLayerException {
        TestSubscriber<List<Repository>> testSubscriber = new TestSubscriber<>();
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
    }

    @Test
    public void givenAnyObservable_whenLift_thenEmitSameItems() throws DataLayerException {
        TestSubscriber<List<Repository>> testSubscriber = new TestSubscriber<>();
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe(testSubscriber);

        testSubscriber.assertValues((List<Repository>[]) repositoryLists.toArray());
        testSubscriber.assertCompleted();
    }

    @Test
    public void givenAnyObservableAndFailingPersist_whenLift_thenEmitError() throws DataLayerException {
        when(repoDataLayer.updateOrInsert(anyListOfModelRepository())).thenThrow(DataLayerException.class);

        TestSubscriber<List<Repository>> testSubscriber = new TestSubscriber<>();
        ANY_OBSERVABLE
                .lift(operator)
                .subscribe(testSubscriber);

        testSubscriber.assertError(DataLayerException.class);
        testSubscriber.assertNotCompleted();
    }

    private static List<com.novoda.github.reports.data.model.Repository> anyListOfModelRepository() {
        return anyListOf(com.novoda.github.reports.data.model.Repository.class);
    }

}
