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
import com.novoda.github.reports.web.hooks.converter.IssueCommentConverter;
import com.novoda.github.reports.web.hooks.model.IssueComment;

public class IssueCommentPersister implements Persister<IssueComment> {

    private final EventConverter<IssueComment> converter;
    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final RepoDataLayer repoDataLayer;

    public static IssueCommentPersister newInstance(ConnectionManager connectionManager) {
        EventConverter<IssueComment> converter = new IssueCommentConverter();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        return new IssueCommentPersister(converter, eventDataLayer, userDataLayer, repoDataLayer);
    }

    private IssueCommentPersister(EventConverter<IssueComment> converter,
                                   EventDataLayer eventDataLayer,
                                   UserDataLayer userDataLayer,
                                   RepoDataLayer repoDataLayer) {

        this.converter = converter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.repoDataLayer = repoDataLayer;
    }

    @Override
    public void persist(IssueComment issueComment) throws PersistenceException {
        GithubComment githubComment = issueComment.getComment();
        GithubRepository githubRepository = issueComment.getRepository();

        String username = githubComment.getUser().getUsername();

        User dbUser = User.create(githubComment.getUserId(), username);
        Repository dbRepository = Repository.create(githubRepository.getId(), githubRepository.getName(), githubRepository.isPrivateRepo());
        Event dbEvent = convertFrom(issueComment);

        try {
            userDataLayer.updateOrInsert(dbUser);
            repoDataLayer.updateOrInsert(dbRepository);
            eventDataLayer.updateOrInsert(dbEvent);
        } catch (DataLayerException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    private Event convertFrom(IssueComment issueComment) throws PersistenceException {
        try {
            return converter.convertFrom(issueComment);
        } catch (ConverterException e) {
            throw new PersistenceException(issueComment);
        }
    }
}

