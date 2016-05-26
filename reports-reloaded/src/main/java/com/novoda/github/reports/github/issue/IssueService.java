package com.novoda.github.reports.github.issue;

import java.util.Date;

import rx.Observable;

interface IssueService {

    Observable<Issue> getPagedIssuesFor(String organisation, String repository);

    Observable<Issue> getPagedIssuesFor(String organisation, String repository, Date since);

    Observable<Event> getPagedEventsFor(String organisation, String repository, Integer issueNumber);

    Observable<Comment> getPagedCommentsFor(String organisation, String repository, Integer issueNumber);

}
