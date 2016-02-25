package com.novoda.reports.organisation;

import com.novoda.reports.RateLimitRetryer;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.NoSuchPageException;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class RepoWebServiceDataSource {

    private static final int MAX_SIZE = 100;

    private final RepositoryService repositoryService;
    private final Converter converter;
    private final RateLimitRetryer rateLimitRetryer;

    RepoWebServiceDataSource(RepositoryService repositoryService, Converter converter, RateLimitRetryer rateLimitRetryer) {
        this.repositoryService = repositoryService;
        this.converter = converter;
        this.rateLimitRetryer = rateLimitRetryer;
    }

    public void createRepositories(String organisation, List<OrganisationRepo> repositories) {
        throw new IllegalStateException("Not supported in this app.");
    }

    public List<OrganisationRepo> readRepositories(String organisation) {
        return readRepositories(organisation, new ArrayList<>(), 1);
    }

    private List<OrganisationRepo> readRepositories(String organisation, List<Repository> elements, int page) {
        try {
            PageIterator<Repository> iterator = repositoryService.pageOrgRepositories(organisation, page, MAX_SIZE);
            while (iterator.hasNext()) {
                Collection<Repository> next = iterator.next();
                elements.addAll(next);
                page++;
            }
        } catch (NoSuchPageException pageException) {
            IOException cause = pageException.getCause();
            if (rateLimitRetryer.hasHitRateLimit()) {
                rateLimitRetryer.retry(organisation, elements, page, this::readRepositories);
            } else {
                throw new IllegalStateException("Failed to get repositories for " + organisation, cause);
            }
        }

        return elements
                .parallelStream()
                .map(converter::convert)
                .collect(Collectors.toList());
    }

    static class Converter {

        public OrganisationRepo convert(Repository repository) {
            String login = repository.getOwner().getLogin();
            String name = repository.getName();
            return new OrganisationRepo(login, name);
        }

    }
}
