package com.novoda.github.reports.github.issue;

import com.novoda.github.reports.github.repository.Repository;

import rx.Observable;
import rx.schedulers.Schedulers;

public class IssuesServiceClient {

    private final IssueService issueService;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        return new IssuesServiceClient(issueService);
    }

    IssuesServiceClient(IssueService issueService) {
        this.issueService = issueService;
    }

    public Observable<Issue> getIssuesFrom(String organisation, String repository) {
        return issueService.getPagedIssuesFor(organisation, repository)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    public Observable<Issue> getIssuesFrom(String organisation, Repository repository) {
        return getIssuesFrom(organisation, repository.getName());
    }
}
