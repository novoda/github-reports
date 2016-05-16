package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.RepositoriesListener;

public interface GithubRepositoryService {

    void getRepositoriesFrom(String organisation, RepositoriesListener repositoriesListener);

}
