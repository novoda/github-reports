package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.issue.Issue;
import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.data.model.DatabaseUser;

public class UserConverter implements Converter<RepositoryIssue, DatabaseUser> {

    public static Converter<RepositoryIssue, DatabaseUser> newInstance() {
        return new UserConverter();
    }

    @Override
    public DatabaseUser convertFrom(RepositoryIssue repositoryIssue) {
        Issue issue = repositoryIssue.getIssue();
        com.novoda.github.reports.batch.User user = issue.getUser();
        return DatabaseUser.create(user.getId(), user.getUsername());
    }

}
