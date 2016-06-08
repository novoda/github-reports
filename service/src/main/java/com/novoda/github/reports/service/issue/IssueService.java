package com.novoda.github.reports.service.issue;

import java.util.Date;

import rx.Observable;

public interface IssueService {

    Observable<GithubIssue> getIssuesFor(String organisation, String repository);

    Observable<GithubIssue> getIssuesFor(String organisation, String repository, Date since);

    Observable<GithubEvent> getEventsFor(String organisation, String repository, Integer issueNumber);

    Observable<GithubEvent> getEventsFor(String organisation, String repository, Integer issueNumber, Date since);

    Observable<GithubComment> getCommentsFor(String organisation, String repository, Integer issueNumber);

    Observable<GithubComment> getCommentsFor(String organisation, String repository, Integer issueNumber, Date since);

}
