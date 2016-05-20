package com.novoda.github.reports.github.repository;

import java.util.List;

import rx.Observable;

interface RepositoryService {

    Observable<List<Repository>> getRepositoriesFrom(String organisation);

}
