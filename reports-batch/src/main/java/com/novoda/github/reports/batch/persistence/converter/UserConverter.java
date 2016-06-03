package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.issue.Issue;
import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.data.model.User;

public class UserConverter implements Converter<RepositoryIssue, User> {

    public static Converter<RepositoryIssue, User> newInstance() {
        return new UserConverter();
    }

    @Override
    public User convertFrom(RepositoryIssue repositoryIssue) {
        Issue issue = repositoryIssue.getIssue();
        com.novoda.github.reports.batch.User user = issue.getUser();
        return User.create(user.getId(), user.getUsername());
    }

}
