package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.GithubUser;
import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.model.DatabaseUser;

public class EventUserConverter implements Converter<RepositoryIssueEvent, DatabaseUser> {

    public static EventUserConverter newInstance() {
        return new EventUserConverter();
    }

    @Override
    public DatabaseUser convertFrom(RepositoryIssueEvent repositoryIssueEvent) {
        GithubUser originalUser = repositoryIssueEvent.getUser();
        return DatabaseUser.create(originalUser.getId(), originalUser.getUsername());
    }

}
