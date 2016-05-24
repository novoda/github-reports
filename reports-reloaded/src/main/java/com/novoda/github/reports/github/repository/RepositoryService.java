package com.novoda.github.reports.github.repository;

import rx.Observable;

interface RepositoryService {

    Observable<Repository> getPagedRepositoriesFor(String organisation);

}
