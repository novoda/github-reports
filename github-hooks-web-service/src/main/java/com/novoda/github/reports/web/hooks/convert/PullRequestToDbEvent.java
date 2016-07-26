package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.PullRequest;

public class PullRequestToDbEvent implements EventConverter<PullRequest, Event> {

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

    @Override
    public EventType convertAction(GithubAction action) {
        switch (action) {
            case ADDED:
                //return EventType.PULL_REQUEST_LABEL_ADD ?
                break;
            case CLOSED:

                break;
            case CREATED:

                break;
            case OPENED:

                break;
            case PUBLISHED:

                break;
            default:
                // TODO throw exception?
        }
    }
}
