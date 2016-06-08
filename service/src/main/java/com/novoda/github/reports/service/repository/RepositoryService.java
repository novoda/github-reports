package com.novoda.github.reports.service.repository;

import rx.Observable;

public interface RepositoryService {

    Observable<GithubRepository> getRepositoriesFor(String organisation);

}
