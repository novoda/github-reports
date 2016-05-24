package com.novoda.github.reports.github.issue;

import rx.Observable;

interface IssueService {

    Observable<Issue> getPagedIssuesFor(String organisation, String repository);

}
