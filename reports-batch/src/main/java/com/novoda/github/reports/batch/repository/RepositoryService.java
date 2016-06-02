package com.novoda.github.reports.batch.repository;

import rx.Observable;

interface RepositoryService {

    Observable<Repository> getPagedRepositoriesFor(String organisation);

}
