package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;

import java.util.Date;

import retrofit2.Response;
import rx.Observable;

public class EventsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final IssueService issueService;

    public static EventsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        return new EventsServiceClient(issueService);
    }

    private EventsServiceClient(IssueService issueService) {
        this.issueService = issueService;
    }

    public Observable<GithubEvent> retrieveEventsFrom(RepositoryIssue repositoryIssue, Date since, int page) {
        String organisation = repositoryIssue.getOwnerUsername();
        String repositoryName = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return issueService.getEventsFor(organisation, repositoryName, issueNumber, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .filter(event -> since == null || event.getCreatedAt().after(since));
    }

}
