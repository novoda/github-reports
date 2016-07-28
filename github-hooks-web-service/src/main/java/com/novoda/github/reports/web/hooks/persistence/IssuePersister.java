package com.novoda.github.reports.web.hooks.persistence;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.web.hooks.converter.EventConverter;
import com.novoda.github.reports.web.hooks.model.Issue;

public class IssuePersister implements Persister<Issue> {

    private final EventConverter<Issue> converter;
    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final RepoDataLayer repoDataLayer;

    public IssuePersister(EventConverter<Issue> converter, EventDataLayer eventDataLayer, UserDataLayer userDataLayer, RepoDataLayer repoDataLayer) {
        this.converter = converter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.repoDataLayer = repoDataLayer;
    }

    @Override
    public void persist(Issue event) throws PersistenceException {
        // TODO
    }
}
