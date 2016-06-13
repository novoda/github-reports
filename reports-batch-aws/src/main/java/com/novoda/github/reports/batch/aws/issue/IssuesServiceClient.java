package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.persistence.IssuePersister;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.Date;

import retrofit2.Response;
import rx.Observable;

public class IssuesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;

    private final IssueService issueService;
    private final DateToISO8601Converter dateConverter;
    private final IssuePersister issuePersister;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        IssuePersister issuePersister = IssuePersister.newInstance();
        return new IssuesServiceClient(issueService, dateConverter, issuePersister);
    }

    private IssuesServiceClient(IssueService issueService, DateToISO8601Converter dateConverter, IssuePersister issuePersister) {
        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.issuePersister = issuePersister;
    }

    public Observable<RepositoryIssue> retrieveIssuesFrom(GithubRepository repository, Date since, int page) {
        String organisation = repository.getOwnerUsername();
        String repositoryName = repository.getName();
        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return issueService.getIssuesFor(organisation, repositoryName, DEFAULT_STATE, date, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .map(issue -> new RepositoryIssue(repository, issue))
                .compose(issuePersister);
    }

}
