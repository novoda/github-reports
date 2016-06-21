package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.lambda.persistence.ResponsePersistTransformer;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.persistence.RepositoryIssuePersistTransformer;

import rx.Observable;

class ResponseRepositoryIssuePersistTransformer extends ResponsePersistTransformer<RepositoryIssue> {

    public static ResponseRepositoryIssuePersistTransformer newInstance(DatabaseCredentialsReader databaseCredentialsReader) {
        RepositoryIssuePersistTransformer persistTransformer = RepositoryIssuePersistTransformer.newInstance(databaseCredentialsReader);
        return new ResponseRepositoryIssuePersistTransformer(persistTransformer);
    }

    public static ResponseRepositoryIssuePersistTransformer newInstance() {
        RepositoryIssuePersistTransformer persistTransformer = RepositoryIssuePersistTransformer.newInstance();
        return new ResponseRepositoryIssuePersistTransformer(persistTransformer);
    }

    private ResponseRepositoryIssuePersistTransformer(Observable.Transformer<RepositoryIssue, RepositoryIssue> persistTransformer) {
        super(persistTransformer);
    }

}
