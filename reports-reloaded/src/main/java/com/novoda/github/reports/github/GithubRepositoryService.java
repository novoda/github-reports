package com.novoda.github.reports.github;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.service.RepositoryService;

public class GithubRepositoryService {

    private RepositoryService repositoryService;
    private IRepositoryIdProvider repositoryIdProvider;

    public static GithubRepositoryService newInstance(String repo) {
        RepositoryName repositoryName = new RepositoryName(repo);
        RepositoryService repositoryService = new RepositoryService(ClientContainer.INSTANCE.getClient());
        return new GithubRepositoryService(repositoryService, repositoryName);
    }

    GithubRepositoryService(RepositoryService repositoryService, IRepositoryIdProvider repositoryIdProvider) {
        this.repositoryService = repositoryService;
        this.repositoryIdProvider = repositoryIdProvider;
    }

}
