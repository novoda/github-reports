package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.GithubUser;
import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.model.User;

public class EventUserConverter implements Converter<RepositoryIssueEvent, User> {

    public static EventUserConverter newInstance() {
        return new EventUserConverter();
    }

    @Override
    public User convertFrom(RepositoryIssueEvent repositoryIssueEvent) {
        GithubUser originalUser = repositoryIssueEvent.getUser();
        return User.create(originalUser.getId(), originalUser.getUsername());
    }

}
