package com.novoda.github.reports.batch.issue;

import java.util.Date;

import rx.Observable;

interface IssueService {

    Observable<Issue> getIssuesFor(String organisation, String repository);

    Observable<Issue> getIssuesFor(String organisation, String repository, Date since);

    Observable<Event> getEventsFor(String organisation, String repository, Integer issueNumber);

    Observable<Event> getEventsFor(String organisation, String repository, Integer issueNumber, Date since);

    Observable<Comment> getCommentsFor(String organisation, String repository, Integer issueNumber);

    Observable<Comment> getCommentsFor(String organisation, String repository, Integer issueNumber, Date since);

}
