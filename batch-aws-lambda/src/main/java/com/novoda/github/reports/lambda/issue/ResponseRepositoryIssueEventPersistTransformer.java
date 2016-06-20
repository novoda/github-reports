package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.lambda.persistence.ResponsePersistTransformer;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.persistence.RepositoryIssueEventPersistTransformer;

import rx.Observable;

public class ResponseRepositoryIssueEventPersistTransformer extends ResponsePersistTransformer<RepositoryIssueEvent> {

    public static ResponseRepositoryIssueEventPersistTransformer newInstance() {
        RepositoryIssueEventPersistTransformer persistTransformer = RepositoryIssueEventPersistTransformer.newInstance();
        return new ResponseRepositoryIssueEventPersistTransformer(persistTransformer);
    }

    private ResponseRepositoryIssueEventPersistTransformer(Observable.Transformer<RepositoryIssueEvent, RepositoryIssueEvent> persistTransformer) {
        super(persistTransformer);
    }

}
