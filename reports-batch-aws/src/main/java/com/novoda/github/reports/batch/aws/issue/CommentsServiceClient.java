package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.persistence.EventPersister;

import java.util.Date;

import retrofit2.Response;
import rx.Observable;

public class CommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final IssueService issueService;
    private final DateToISO8601Converter dateConverter;
    private final EventPersister eventPersister;

    public static CommentsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        EventPersister eventPersister = EventPersister.newInstance();
        return new CommentsServiceClient(issueService, dateConverter, eventPersister);
    }

    public CommentsServiceClient(IssueService issueService, DateToISO8601Converter dateConverter, EventPersister eventPersister) {
        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.eventPersister = eventPersister;
    }

    public Observable<RepositoryIssueEvent> retrieveCommentsAsEventsFrom(RepositoryIssue repositoryIssue, Date since, int page) {
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return issueService.getCommentsFor(organisation, repository, issueNumber, date, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .map(comment -> new RepositoryIssueEventComment(repositoryIssue, comment))
                .compose(eventPersister);
    }

}
