package com.novoda.github.reports.batch.aws.pullrequest;

import com.novoda.github.reports.batch.aws.ResponsePersistTransformer;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.persistence.RepositoryIssueEventPersistTransformer;

import rx.Observable;

class ResponseRepositoryIssueEventPersistTransformer extends ResponsePersistTransformer<RepositoryIssueEvent> {

    public static ResponseRepositoryIssueEventPersistTransformer newInstance() {
        RepositoryIssueEventPersistTransformer persistTransformer = RepositoryIssueEventPersistTransformer.newInstance();
        return new ResponseRepositoryIssueEventPersistTransformer(persistTransformer);
    }

    private ResponseRepositoryIssueEventPersistTransformer(Observable.Transformer<RepositoryIssueEvent, RepositoryIssueEvent> persistTransformer) {
        super(persistTransformer);
    }

}
