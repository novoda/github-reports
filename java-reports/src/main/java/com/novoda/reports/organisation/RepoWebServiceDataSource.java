package com.novoda.reports.organisation;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class RepoWebServiceDataSource {
    private final RepositoryService repositoryService;
    private final Converter converter;

    RepoWebServiceDataSource(RepositoryService repositoryService, Converter converter) {
        this.repositoryService = repositoryService;
        this.converter = converter;
    }

    public void createRepositories(String organisation, List<OrganisationRepo> repositories) {
        throw new IllegalStateException("Not supported in this app.");
    }

    public List<OrganisationRepo> readRepositories(String organisation) {
        try {
            return repositoryService.getOrgRepositories(organisation)
                    .parallelStream()
                    .map(converter::convert)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to get repositories for " + organisation, e);
        }
    }

    static class Converter {

        public OrganisationRepo convert(Repository repository) {
            String login = repository.getOwner().getLogin();
            String name = repository.getName();
            return new OrganisationRepo(login, name);
        }

    }
}
