package com.novoda.reports;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.List;

class OrganisationRepositoryFinder {

    private final String organisation;
    private final RepositoryService repositoryService;

    OrganisationRepositoryFinder(String organisation, RepositoryService repositoryService) {
        this.organisation = organisation;
        this.repositoryService = repositoryService;
    }

    public List<Repository> getOrganisationRepositories() {
        try {
            return repositoryService.getOrgRepositories(organisation);
        } catch (IOException e) {
            throw new IllegalStateException("Foo get repositories for " + organisation, e);
        }
    }

}
