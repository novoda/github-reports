package com.novoda.github.reports.batch;

import com.novoda.github.reports.batch.issue.IssuesServiceClient;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.batch.repository.RepositoriesServiceClient;

import java.util.Collections;
import java.util.Date;

import rx.Observable;

class BatchClient {

    private static final IssuesServiceClient issueServiceClient = IssuesServiceClient.newInstance();
    private static final RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();

    private BatchClient() {
        // non-instantiable
    }

    static Observable<RepositoryIssueEvent> retrieve(String organisation, Date since) {
        return Observable.from(Collections.singletonList(organisation))
                .compose(retrieveRepositoriesFromOrganizations())
                .compose(retrieveIssuesFromRepositories(since))
                .compose(retrieveEventsFromIssues(since));
    }

    private static Observable.Transformer<? super String, ? extends GithubRepository> retrieveRepositoriesFromOrganizations() {
        return organizationObservable ->
                organizationObservable.flatMap(repositoriesServiceClient::retrieveRepositoriesFrom);
    }

    private static Observable.Transformer<? super GithubRepository, ? extends RepositoryIssue> retrieveIssuesFromRepositories(Date since) {
        return repositoryObservable ->
                repositoryObservable.flatMap(repository -> issueServiceClient.retrieveIssuesFrom(repository, since));
    }

    private static Observable.Transformer<? super RepositoryIssue, ? extends RepositoryIssueEvent> retrieveEventsFromIssues(Date since) {
        return repositoryIssueObservable -> {

            Observable<RepositoryIssueEvent> comments = repositoryIssueObservable
                    .flatMap(repositoryIssue -> issueServiceClient.retrieveCommentsFrom(repositoryIssue, since));

            Observable<RepositoryIssueEvent> events = repositoryIssueObservable
                    .flatMap(repositoryIssue -> issueServiceClient.retrieveEventsFrom(repositoryIssue, since));

            return Observable.merge(comments, events);
        };
    }

}
