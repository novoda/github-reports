package com.novoda.github.reports.batch.github.repository;

import rx.Observable;

interface RepositoryService {

    Observable<Repository> getPagedRepositoriesFor(String organisation);

}
