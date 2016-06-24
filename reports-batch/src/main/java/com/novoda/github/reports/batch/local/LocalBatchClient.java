package com.novoda.github.reports.batch.local;

import com.novoda.github.reports.batch.local.issue.CommentsServiceClient;
import com.novoda.github.reports.batch.local.issue.EventsServiceClient;
import com.novoda.github.reports.batch.local.issue.IssuesServiceClient;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.batch.local.repository.RepositoriesServiceClient;

import java.util.Collections;
import java.util.Date;

import rx.Observable;

public class LocalBatchClient {

    private static final IssuesServiceClient ISSUE_SERVICE_CLIENT = IssuesServiceClient.newInstance();
    private static final RepositoriesServiceClient REPOSITORY_SERVICE_CLIENT = RepositoriesServiceClient.newInstance();
    private static final CommentsServiceClient COMMENTS_SERVICE_CLIENT = CommentsServiceClient.newInstance();
    private static final EventsServiceClient EVENTS_SERVICE_CLIENT = EventsServiceClient.newInstance();

    private LocalBatchClient() {
        // non-instantiable
    }

    public static Observable<RepositoryIssueEvent> retrieve(String organisation, Date since) {
        return Observable.from(Collections.singletonList(organisation))
                .compose(retrieveRepositoriesFromOrganizations())
                .compose(retrieveIssuesFromRepositories(since))
                .compose(retrieveEventsFromIssues(since));
    }

    private static Observable.Transformer<? super String, ? extends GithubRepository> retrieveRepositoriesFromOrganizations() {
        return organizationObservable ->
                organizationObservable.flatMap(REPOSITORY_SERVICE_CLIENT::retrieveRepositoriesFrom);
    }

    private static Observable.Transformer<? super GithubRepository, ? extends RepositoryIssue> retrieveIssuesFromRepositories(Date since) {
        return repositoryObservable ->
                repositoryObservable.flatMap(repository -> ISSUE_SERVICE_CLIENT.retrieveIssuesFrom(repository, since));
    }

    private static Observable.Transformer<? super RepositoryIssue, ? extends RepositoryIssueEvent> retrieveEventsFromIssues(Date since) {
        return repositoryIssueObservable -> {

            Observable<RepositoryIssueEvent> comments = repositoryIssueObservable
                    .flatMap(repositoryIssue -> COMMENTS_SERVICE_CLIENT.retrieveCommentsAsEventsFrom(repositoryIssue, since));

            Observable<RepositoryIssueEvent> events = repositoryIssueObservable
                    .flatMap(repositoryIssue -> EVENTS_SERVICE_CLIENT.retrieveEventsFrom(repositoryIssue, since));

            return Observable.merge(comments, events);
        };
    }

}
