package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventEvent;
import com.novoda.github.reports.service.persistence.EventsPersister;

import java.util.Date;

import retrofit2.Response;
import rx.Observable;

public class EventsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final IssueService issueService;
    private final EventsPersister eventsPersister;

    public static EventsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        EventsPersister eventsPersister = EventsPersister.newInstance();
        return new EventsServiceClient(issueService, eventsPersister);
    }

    public EventsServiceClient(IssueService issueService, EventsPersister eventsPersister) {
        this.issueService = issueService;
        this.eventsPersister = eventsPersister;
    }

    public Observable<RepositoryIssueEvent> retrieveEventsFrom(RepositoryIssue repositoryIssue, Date since, int page) {
        String organisation = repositoryIssue.getOwnerUsername();
        String repositoryName = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return issueService.getEventsFor(organisation, repositoryName, issueNumber, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .filter(event -> since == null || event.getCreatedAt().after(since))
                .map(event -> RepositoryIssueEventEvent.newInstance(repositoryIssue, event))
                .compose(eventsPersister);
    }

}
