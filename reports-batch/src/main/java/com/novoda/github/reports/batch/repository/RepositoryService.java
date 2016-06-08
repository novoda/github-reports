package com.novoda.github.reports.batch.repository;

import rx.Observable;

interface RepositoryService {

    Observable<GithubRepository> getRepositoriesFor(String organisation);

}
