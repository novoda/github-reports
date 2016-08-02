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
import com.novoda.github.reports.web.hooks.converter.ReviewCommentConverter;
import com.novoda.github.reports.web.hooks.converter.EventConverter;
import com.novoda.github.reports.web.hooks.model.ReviewComment;

public class ReviewCommentPersister implements Persister<ReviewComment> {

    private final EventConverter<ReviewComment> converter;
    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final RepoDataLayer repoDataLayer;

    public static ReviewCommentPersister newInstance(ConnectionManager connectionManager) {
        EventConverter<ReviewComment> converter = new ReviewCommentConverter();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        return new ReviewCommentPersister(converter, eventDataLayer, userDataLayer, repoDataLayer);
    }

    private ReviewCommentPersister(EventConverter<ReviewComment> converter,
                                   EventDataLayer eventDataLayer,
                                   UserDataLayer userDataLayer,
                                   RepoDataLayer repoDataLayer) {

        this.converter = converter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.repoDataLayer = repoDataLayer;
    }

    @Override
    public void persist(ReviewComment reviewComment) throws PersistenceException {
        GithubComment githubComment = reviewComment.getComment();
        GithubRepository repository = reviewComment.getRepository();
        String username = githubComment.getUser().getUsername();

        User dbUser = User.create(githubComment.getUserId(), username);
        Repository dbRepository = Repository.create(repository.getId(), repository.getName(), repository.isPrivateRepo());
        Event dbEvent = convertFrom(reviewComment);

        try {
            userDataLayer.updateOrInsert(dbUser);
            repoDataLayer.updateOrInsert(dbRepository);
            eventDataLayer.updateOrInsert(dbEvent);
        } catch (DataLayerException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    private Event convertFrom(ReviewComment reviewComment) throws PersistenceException {
        try {
            return converter.convertFrom(reviewComment);
        } catch (ConverterException e) {
            // TODO swallow this exception 'cause it should be from actions we don't support
            throw new PersistenceException(reviewComment);
        }
    }
}
