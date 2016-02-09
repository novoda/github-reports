package com.novoda.reports.organisation;

import org.eclipse.egit.github.core.service.RepositoryService;

import java.util.List;

public class OrganisationRepoFinder {

    private final String organisation;
    private final RepoInMemoryDataSource inMemoryDataSource;
    private final RepoPersistenceDataSource persistenceDataSource;
    private final RepoWebServiceDataSource webServiceDataSource;

    public static OrganisationRepoFinder newInstance(String organisation, RepositoryService repositoryService) {
        RepoInMemoryDataSource repoInMemoryDataSource = new RepoInMemoryDataSource();
        RepoSqlite3Persistence persistence = new RepoSqlite3Persistence();
        RepoPersistenceDataSource repoPersistenceDataSource = new RepoPersistenceDataSource(persistence);
        RepoWebServiceDataSource.Converter converter = new RepoWebServiceDataSource.Converter();
        RepoWebServiceDataSource repoWebServiceDataSource = new RepoWebServiceDataSource(repositoryService, converter);
        return new OrganisationRepoFinder(organisation, repoInMemoryDataSource, repoPersistenceDataSource, repoWebServiceDataSource);
    }

    public OrganisationRepoFinder(String organisation,
                                  RepoInMemoryDataSource repoInMemoryDataSource,
                                  RepoPersistenceDataSource repoPersistenceDataSource,
                                  RepoWebServiceDataSource repoWebServiceDataSource) {
        this.organisation = organisation;
        this.inMemoryDataSource = repoInMemoryDataSource;
        this.persistenceDataSource = repoPersistenceDataSource;
        this.webServiceDataSource = repoWebServiceDataSource;
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
