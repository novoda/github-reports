package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.convert.EventConverter;
import com.novoda.github.reports.web.hooks.convert.PullRequestToDbEventConverter;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PullRequestExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.PullRequest;

class PullRequestHandler implements EventHandler {

    private final PullRequestExtractor extractor;
    private final EventConverter<PullRequest, Event> converter;

    private final DbEventDataLayer eventDataLayer;
    private final DbUserDataLayer userDataLayer;
    private final DbRepoDataLayer repoDataLayer;

    static PullRequestHandler newInstance(ConnectionManager connectionManager) {
        PullRequestExtractor pullRequestExtractor = new PullRequestExtractor();
        EventConverter<PullRequest, Event> converter = new PullRequestToDbEventConverter();
        DbEventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        DbUserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        DbRepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        return new PullRequestHandler(pullRequestExtractor, converter, eventDataLayer, userDataLayer, repoDataLayer);
    }

    PullRequestHandler(PullRequestExtractor extractor,
                       EventConverter<PullRequest, Event> converter,
                       DbEventDataLayer eventDataLayer,
                       DbUserDataLayer userDataLayer,
                       DbRepoDataLayer repoDataLayer) {
        this.extractor = extractor;
        this.converter = converter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.repoDataLayer = repoDataLayer;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {

        PullRequest pullRequest = extractPullRequest(event);
        GithubIssue issue = pullRequest.getIssue();
        GithubRepository repository = pullRequest.getRepository();

        Event dbEvent = convertFrom(pullRequest);

        // TODO @RUI extract persistence operations to their own classes
        User dbUser = User.create(issue.getUserId(), issue.getUser().getUsername());
        Repository dbRepository = Repository.create(repository.getId(), repository.getName(), repository.isPrivateRepo());

        try {
            userDataLayer.updateOrInsert(dbUser);
            repoDataLayer.updateOrInsert(dbRepository);
            eventDataLayer.updateOrInsert(dbEvent);
        } catch (DataLayerException e) {
            e.printStackTrace();
            throw new UnhandledEventException(e.getMessage());
        }
    }

    private Event convertFrom(PullRequest pullRequest) throws UnhandledEventException {
        try {
            return converter.convertFrom(pullRequest);
        } catch (ConverterException e) {
            // TODO swallow this exception
            throw new UnhandledEventException(e.getMessage());
        }
    }

    private PullRequest extractPullRequest(GithubWebhookEvent event) throws UnhandledEventException {
        try {
            return extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
    }

    @Override
    public EventType handledEventType() {
        return EventType.PULL_REQUEST;
    }

}
