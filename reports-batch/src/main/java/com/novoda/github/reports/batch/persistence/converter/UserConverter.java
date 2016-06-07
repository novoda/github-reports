package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.GithubUser;
import com.novoda.github.reports.batch.issue.GithubIssue;
import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.data.model.User;

public class UserConverter implements Converter<RepositoryIssue, User> {

    public static Converter<RepositoryIssue, User> newInstance() {
        return new UserConverter();
    }

    @Override
    public User convertFrom(RepositoryIssue repositoryIssue) {
        GithubIssue issue = repositoryIssue.getIssue();
        GithubUser user = issue.getUser();
        return User.create(user.getId(), user.getUsername());
    }

}
