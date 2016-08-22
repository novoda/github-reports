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
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.converter.EventConverter;
import com.novoda.github.reports.web.hooks.converter.PullRequestCommentConverter;
import com.novoda.github.reports.web.hooks.model.PullRequestComment;

public class PullRequestCommentPersister implements Persister<PullRequestComment> {

    private final EventConverter<PullRequestComment> converter;
    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final RepoDataLayer repoDataLayer;

    public static PullRequestCommentPersister newInstance(ConnectionManager connectionManager) {
        EventConverter<PullRequestComment> converter = new PullRequestCommentConverter();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        return new PullRequestCommentPersister(converter, eventDataLayer, userDataLayer, repoDataLayer);
    }

    private PullRequestCommentPersister(EventConverter<PullRequestComment> converter,
                                        EventDataLayer eventDataLayer,
                                        UserDataLayer userDataLayer,
                                        RepoDataLayer repoDataLayer) {

        this.converter = converter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.repoDataLayer = repoDataLayer;
    }

    @Override
    public void persist(PullRequestComment pullRequestComment) throws PersistenceException {
        GithubComment githubComment = pullRequestComment.getComment();
        GithubRepository githubRepository = pullRequestComment.getRepository();

        String username = githubComment.getUser().getUsername();

        User dbUser = User.create(githubComment.getUserId(), username);
        Repository dbRepository = Repository.create(githubRepository.getId(), githubRepository.getName(), githubRepository.isPrivateRepo());
        Event dbEvent = convertFrom(pullRequestComment);

        try {
            userDataLayer.updateOrInsert(dbUser);
            repoDataLayer.updateOrInsert(dbRepository);
            eventDataLayer.updateOrInsert(dbEvent);
        } catch (DataLayerException e) {
            throw new PersistenceException(e);
        }
    }

    private Event convertFrom(PullRequestComment pullRequestComment) throws PersistenceException {
        try {
            return converter.convertFrom(pullRequestComment);
        } catch (ConverterException e) {
            throw new PersistenceException(e);
        }
    }
}

