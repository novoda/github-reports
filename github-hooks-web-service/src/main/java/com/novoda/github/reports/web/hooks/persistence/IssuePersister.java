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
import com.novoda.github.reports.web.hooks.converter.EventConverter;
import com.novoda.github.reports.web.hooks.converter.IssueConverter;
import com.novoda.github.reports.web.hooks.model.Issue;

public class IssuePersister implements Persister<Issue> {

    private final EventConverter<Issue> converter;
    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final RepoDataLayer repoDataLayer;

    public static IssuePersister newInstance(ConnectionManager connectionManager) {
        EventConverter<Issue> converter = new IssueConverter();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        return new IssuePersister(converter, eventDataLayer, userDataLayer, repoDataLayer);
    }

    private IssuePersister(EventConverter<Issue> converter,
                           EventDataLayer eventDataLayer,
                           UserDataLayer userDataLayer,
                           RepoDataLayer repoDataLayer) {

        this.converter = converter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.repoDataLayer = repoDataLayer;
    }

    @Override
    public void persist(Issue issue) throws PersistenceException {
        GithubIssue githubIssue = issue.getIssue();
        GithubRepository repository = issue.getRepository();
        String username = githubIssue.getUser().getUsername();

        User dbUser = User.create(githubIssue.getUserId(), username);
        Repository dbRepository = Repository.create(repository.getId(), repository.getName(), repository.isPrivateRepo());
        Event dbEvent = convertFrom(issue);

        try {
            userDataLayer.updateOrInsert(dbUser);
            repoDataLayer.updateOrInsert(dbRepository);
            eventDataLayer.updateOrInsert(dbEvent);
        } catch (DataLayerException e) {
            throw new PersistenceException(e);
        }
    }

    private Event convertFrom(Issue issue) throws PersistenceException {
        try {
            return converter.convertFrom(issue);
        } catch (ConverterException e) {
            throw new PersistenceException(e);
        }
    }
}
