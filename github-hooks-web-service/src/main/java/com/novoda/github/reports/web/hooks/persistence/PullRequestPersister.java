package com.novoda.github.reports.web.hooks.persistence;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
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
import com.novoda.github.reports.web.hooks.convert.EventConverter;
import com.novoda.github.reports.web.hooks.convert.PullRequestToDbEventConverter;
import com.novoda.github.reports.web.hooks.model.PullRequest;

public class PullRequestPersister implements Persister<PullRequest> {

    private final EventConverter<PullRequest> converter;
    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final RepoDataLayer repoDataLayer;

    public static PullRequestPersister newInstance(ConnectionManager connectionManager) {
        EventConverter<PullRequest> converter = new PullRequestToDbEventConverter();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        return new PullRequestPersister(converter, eventDataLayer, userDataLayer, repoDataLayer);
    }

    PullRequestPersister(EventConverter<PullRequest> converter,
                         EventDataLayer eventDataLayer,
                         UserDataLayer userDataLayer,
                         RepoDataLayer repoDataLayer) {

        this.converter = converter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.repoDataLayer = repoDataLayer;
    }

    @Override
    public void persist(PullRequest pullRequest) throws PersistenceException {
        Event dbEvent = convertFrom(pullRequest);

        GithubIssue issue = pullRequest.getIssue();
        GithubRepository repository = pullRequest.getRepository();

        User dbUser = User.create(issue.getUserId(), issue.getUser().getUsername());
        Repository dbRepository = Repository.create(repository.getId(), repository.getName(), repository.isPrivateRepo());

        try {
            userDataLayer.updateOrInsert(dbUser);
            repoDataLayer.updateOrInsert(dbRepository);
            eventDataLayer.updateOrInsert(dbEvent);
        } catch (DataLayerException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    private Event convertFrom(PullRequest pullRequest) throws PersistenceException {
        try {
            return converter.convertFrom(pullRequest);
        } catch (ConverterException e) {
            // TODO swallow this exception 'cause it should be from actions we don't support
            throw new PersistenceException(pullRequest);
        }
    }
}
