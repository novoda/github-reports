package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.RepositoriesListener;
import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import rx.Observable;

public interface GithubRepositoryService {

    void getRepositories(String organisation, RepositoriesListener repositoriesListener);

    Observable<List<Repository>> getRepositoriesFrom(String organisation);

}
