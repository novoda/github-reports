package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import retrofit2.Response;
import rx.Observable;
import rx.observers.TestObserver;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.mockito.MockitoAnnotations.initMocks;

public class ResponseRepositoryIssuePersistTransformerTest {

    @Mock
    Observable.Transformer<RepositoryIssue, RepositoryIssue> mockPersistTransformer;

    private Response<List<RepositoryIssue>> response;

    private TestObserver<Response<List<RepositoryIssue>>> testObserver;
    private TestSubscriber<Response<List<RepositoryIssue>>> testSubscriber;
    private TestScheduler testScheduler;

    private Observable<Response<List<RepositoryIssue>>> observable;

    private ResponseRepositoryIssuePersistTransformer responsePersistTransformer;

    @Before
    public void setUp() {
        initMocks(this);

        RepositoryIssue aRepositoryIssue = new RepositoryIssue(new GithubRepository(1L), new GithubIssue(1));
        RepositoryIssue anotherRepositoryIssue = new RepositoryIssue(new GithubRepository(2L), new GithubIssue(2));

        List<RepositoryIssue> list = Arrays.asList(aRepositoryIssue, anotherRepositoryIssue);
        response = Response.success(list);

        testScheduler = new TestScheduler();
        testObserver = new TestObserver<>();
        testSubscriber = new TestSubscriber<>(testObserver);
        observable = Observable.from(Collections.singletonList(response));

        responsePersistTransformer = new ResponseRepositoryIssuePersistTransformer(mockPersistTransformer);
    }

    @Test
    public void givenAnObservable_whenTransforming_thenTheOutputIsTheSameAsTheInput() {
        observable.subscribeOn(testScheduler);
        observable.subscribe(testSubscriber);

        responsePersistTransformer.call(observable);

        testSubscriber.assertReceivedOnNext(Collections.singletonList(response));
    }

}
