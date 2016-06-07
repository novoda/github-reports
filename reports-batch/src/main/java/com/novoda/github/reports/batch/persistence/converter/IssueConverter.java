package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.issue.Issue;
import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.model.DatabaseEvent;
import com.novoda.github.reports.data.model.EventType;

public class IssueConverter implements Converter<RepositoryIssue, DatabaseEvent> {

    public static IssueConverter newInstance() {
        return new IssueConverter();
    }

    @Override
    public DatabaseEvent convertFrom(RepositoryIssue repositoryIssue) {
        Issue issue = repositoryIssue.getIssue();
        Repository repo = repositoryIssue.getRepository();
        EventType type = issue.isPullRequest() ? EventType.PULL_REQUEST_OPEN : EventType.ISSUE_OPEN;
        return DatabaseEvent.create(issue.getId(),
                                    repo.getId(),
                                    issue.getUser().getId(),
                                    issue.getUser().getId(),
                                    type,
                                    issue.getCreatedAt());
    }
}
