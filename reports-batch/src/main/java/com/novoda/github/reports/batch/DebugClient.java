package com.novoda.github.reports.batch;

import com.novoda.github.reports.batch.issue.IssuesServiceClient;
import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.repository.RepositoriesServiceClient;
import com.novoda.github.reports.batch.repository.Repository;

import java.util.Collections;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;

class DebugClient {

    private static final Date NO_SINCE_DATE = null;

    private static final IssuesServiceClient issueServiceClient = IssuesServiceClient.newInstance();
    private static final RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();

    private DebugClient() {
        // non-instantiable
    }

    static void retrieve(String organisation, Date since) {
        Observable.from(Collections.singletonList(organisation))
                .compose(retrieveRepositoriesFromOrganizations())
                .compose(retrieveIssuesFromRepositories(since))
                .compose(retrieveEventsFromIssues(since))
                .toBlocking()
                .subscribe(new Subscriber<RepositoryIssueEvent>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> retrieve completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> retrieve error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(RepositoryIssueEvent event) {
                        System.out.println("> retrieve: " + event.getEventType());
                    }
                });
    }

    static void retrieveRepositoriesAndIssues(String organisation, Date since) {
        Observable.from(Collections.singletonList(organisation))
                .compose(retrieveRepositoriesFromOrganizations())
                .compose(retrieveIssuesFromRepositories(since))
                .toBlocking()
                .subscribe(new Subscriber<RepositoryIssue>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> retrieveRepositoriesAndIssues completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> retrieveRepositoriesAndIssues error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(RepositoryIssue issue) {
                        System.out.println("> retrieveRepositoriesAndIssues: " + issue.getIssue());
                    }
                });
    }

    static void retrieveIssues(String organisation, Long repoId, String repoName) {
        Repository repo = new Repository();
        repo.setName(repoName);
        repo.setId(repoId);
        User owner = new User();
        owner.setUsername(organisation);
        repo.setOwner(owner);
        Observable.from(Collections.singletonList(repo))
                .compose(retrieveIssuesFromRepositories(null))
                .toBlocking()
                .subscribe(new Subscriber<RepositoryIssue>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> retrieveIssues completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> retrieveIssues error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(RepositoryIssue issue) {
                        System.out.println("> retrieveIssues: " + issue.getIssue());
                    }
                });
    }

    static void retrieveIssuesAndEvents(String organisation, Long repoId, String repoName) {
        Repository repo = new Repository();
        repo.setName(repoName);
        repo.setId(repoId);
        User owner = new User();
        owner.setUsername(organisation);
        repo.setOwner(owner);
        Observable.from(Collections.singletonList(repo))
                .compose(retrieveIssuesFromRepositories(NO_SINCE_DATE))
                .compose(retrieveEventsFromIssues(NO_SINCE_DATE))
                .toBlocking()
                .subscribe(new Subscriber<RepositoryIssueEvent>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> retrieveIssuesAndEvents completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> retrieveIssuesAndEvents error: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(RepositoryIssueEvent event) {
                        System.out.println("> retrieveIssuesAndEvents: " + event);
                    }
                });
    }

    private static Observable.Transformer<? super String, ? extends Repository> retrieveRepositoriesFromOrganizations() {
        return organizationObservable ->
                organizationObservable.flatMap(repositoriesServiceClient::retrieveRepositoriesFrom);
    }

    private static Observable.Transformer<? super Repository, ? extends RepositoryIssue> retrieveIssuesFromRepositories(Date since) {
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
