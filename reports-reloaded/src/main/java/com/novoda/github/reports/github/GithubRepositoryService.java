package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.RepositoriesListener;

public interface GithubRepositoryService {

    void getRepositories(String organisation, RepositoriesListener repositoriesListener);

    void getRepositoriesFrom(String organisation);

}
