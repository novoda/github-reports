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
            repoDatabase.open();
            repoDatabase.create();
            repoDatabase.update(organisation, organisationRepos);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not save repos to repository.", e);
        } finally {
            repoDatabase.close();
        }
    }

    public List<OrganisationRepo> readRepositories(String organisation) {
        try {
            repoDatabase.open();
            return repoDatabase.read(organisation);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not read repos from repository.", e);
        } finally {
            repoDatabase.close();
        }
    }

    public void delete(String organisation) {
        try {
            repoDatabase.open();
            repoDatabase.delete(organisation);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not delete " + organisation + " from repository.", e);
        } finally {
            repoDatabase.close();
        }
    }
}
