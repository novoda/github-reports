package com.novoda.reports.organisation;

import com.almworks.sqlite4java.SQLiteException;

import java.util.List;

class RepoPersistenceDataSource {

    private final RepoSqlite3Persistence persistence;

    RepoPersistenceDataSource(RepoSqlite3Persistence persistence) {
        this.persistence = persistence;
    }

    public void createRepositories(String organisation, List<OrganisationRepo> organisationRepos) {
        try {
            persistence.create();
            persistence.update(organisation, organisationRepos);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not save repos to repository.", e);
        }
    }

    public List<OrganisationRepo> readRepositories(String organisation) {
        try {
            return persistence.read(organisation);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not read repos from repository.", e);
        }
    }

    public void delete(String organisation) {
        try {
            persistence.delete(organisation);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not delete " + organisation + " from repository.", e);
        }
    }
}
