package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.batch.aws.ResponsePersistTransformer;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.persistence.RepositoryIssuePersistTransformer;

import rx.Observable;

class ResponseRepositoryIssuePersistTransformer extends ResponsePersistTransformer<RepositoryIssue> {

    public static ResponseRepositoryIssuePersistTransformer newInstance() {
        RepositoryIssuePersistTransformer persistTransformer = RepositoryIssuePersistTransformer.newInstance();
        return new ResponseRepositoryIssuePersistTransformer(persistTransformer);
    }

    private ResponseRepositoryIssuePersistTransformer(Observable.Transformer<RepositoryIssue, RepositoryIssue> persistTransformer) {
        super(persistTransformer);
    }

}
