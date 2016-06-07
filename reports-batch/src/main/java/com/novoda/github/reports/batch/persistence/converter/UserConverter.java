package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.GithubUser;
import com.novoda.github.reports.batch.issue.GithubIssue;
import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.data.model.DatabaseUser;

public class UserConverter implements Converter<RepositoryIssue, DatabaseUser> {

    public static Converter<RepositoryIssue, DatabaseUser> newInstance() {
        return new UserConverter();
    }

    @Override
    public DatabaseUser convertFrom(RepositoryIssue repositoryIssue) {
        GithubIssue issue = repositoryIssue.getIssue();
        GithubUser user = issue.getUser();
        return DatabaseUser.create(user.getId(), user.getUsername());
    }

}
