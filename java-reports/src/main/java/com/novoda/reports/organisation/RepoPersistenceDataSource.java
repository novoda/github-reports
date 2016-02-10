package com.novoda.reports.organisation;

import com.almworks.sqlite4java.SQLiteException;

import java.util.List;

class RepoPersistenceDataSource {

    private final RepoSqlite3Database repoDatabase;

    RepoPersistenceDataSource(RepoSqlite3Database repoDatabase) {
        this.repoDatabase = repoDatabase;
    }

    public void createRepositories(String organisation, List<OrganisationRepo> organisationRepos) {
        try {
            repoDatabase.create();
            repoDatabase.update(organisation, organisationRepos);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not save repos to repository.", e);
        }
    }

    public List<OrganisationRepo> readRepositories(String organisation) {
        try {
            return repoDatabase.read(organisation);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not read repos from repository.", e);
        }
    }

    public void delete(String organisation) {
        try {
            repoDatabase.delete(organisation);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not delete " + organisation + " from repository.", e);
        }
    }
}
