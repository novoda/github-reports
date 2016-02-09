package com.novoda.reports.organisation;

import org.eclipse.egit.github.core.service.RepositoryService;

import java.util.List;

public class OrganisationRepoFinder {

    private final String organisation;
    private final RepoInMemoryDataSource inMemoryDataSource;
    private final RepoPersistenceDataSource persistenceDataSource;
    private final RepoWebServiceDataSource webServiceDataSource;

    public OrganisationRepoFinder(String organisation, RepositoryService repositoryService) {
        this.organisation = organisation;
        this.inMemoryDataSource = new RepoInMemoryDataSource();
        this.persistenceDataSource = new RepoPersistenceDataSource(new RepoSqlite3Persistence());
        this.webServiceDataSource = new RepoWebServiceDataSource(repositoryService, new RepoWebServiceDataSource.Converter());
    }

    public List<OrganisationRepo> getOrganisationRepositories() {
        List<OrganisationRepo> inMemoryRepositories = inMemoryDataSource.readRepositories(organisation);
        if (!inMemoryRepositories.isEmpty()) {
            return inMemoryRepositories;
        }
        List<OrganisationRepo> diskRepositories = persistenceDataSource.readRepositories(organisation);
        if (!diskRepositories.isEmpty()) {
            inMemoryDataSource.createRepositories(organisation, diskRepositories);
            return diskRepositories;
        }
        List<OrganisationRepo> webRepositories = webServiceDataSource.readRepositories(organisation);
        persistenceDataSource.createRepositories(organisation, webRepositories);
        inMemoryDataSource.createRepositories(organisation, webRepositories);
        return webRepositories;
    }

}
