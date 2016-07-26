package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.PullRequest;

public class PullRequestToDbEvent implements Converter<PullRequest, Event> {

    @Override
    public Event convertFrom(PullRequest pullRequest) throws ConverterException {

        GithubIssue issue = pullRequest.getIssue();
        GithubRepository repository = pullRequest.getRepository();

        return Event.create(
                issue.getId(),
                repository.getId(),
                issue.getUserId(),
                issue.getUserId(),
                com.novoda.github.reports.data.model.EventType.BRANCH_DELETE, // TODO ActionToEventType
                issue.getUpdatedAt()
        );
    }
}
