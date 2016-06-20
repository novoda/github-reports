package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.data.model.User;

class EventUserConverter implements Converter<RepositoryIssueEvent, User> {

    public static EventUserConverter newInstance() {
        return new EventUserConverter();
    }

    @Override
    public User convertFrom(RepositoryIssueEvent repositoryIssueEvent) {
        GithubUser originalUser = repositoryIssueEvent.getUser();
        return User.create(originalUser.getId(), originalUser.getUsername());
    }

}
