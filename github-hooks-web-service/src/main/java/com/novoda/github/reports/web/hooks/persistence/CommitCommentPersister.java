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
import com.novoda.github.reports.web.hooks.converter.CommitCommentConverter;
import com.novoda.github.reports.web.hooks.converter.EventConverter;
import com.novoda.github.reports.web.hooks.model.CommitComment;

public class CommitCommentPersister implements Persister<CommitComment> {

    private final EventConverter<CommitComment> converter;
    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final RepoDataLayer repoDataLayer;

    public static CommitCommentPersister newInstance(ConnectionManager connectionManager) {
        EventConverter<CommitComment> converter = new CommitCommentConverter();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        return new CommitCommentPersister(converter, eventDataLayer, userDataLayer, repoDataLayer);
    }

    private CommitCommentPersister(EventConverter<CommitComment> converter,
                                   EventDataLayer eventDataLayer,
                                   UserDataLayer userDataLayer,
                                   RepoDataLayer repoDataLayer) {

        this.converter = converter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.repoDataLayer = repoDataLayer;
    }

    @Override
    public void persist(CommitComment commitComment) throws PersistenceException {
        GithubComment githubComment = commitComment.getComment();
        GithubRepository repository = commitComment.getRepository();
        String username = githubComment.getUser().getUsername();

        User dbUser = User.create(githubComment.getUserId(), username);
        Repository dbRepository = Repository.create(repository.getId(), repository.getName(), repository.isPrivateRepo());
        Event dbEvent = convertFrom(commitComment);

        try {
            userDataLayer.updateOrInsert(dbUser);
            repoDataLayer.updateOrInsert(dbRepository);
            eventDataLayer.updateOrInsert(dbEvent);
        } catch (DataLayerException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    private Event convertFrom(CommitComment commitComment) throws PersistenceException {
        try {
            return converter.convertFrom(commitComment);
        } catch (ConverterException e) {
            // TODO swallow this exception 'cause it should be from actions we don't support
            throw new PersistenceException(commitComment);
        }
    }
}
