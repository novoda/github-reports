package com.novoda.github.reports.github.repository;

import java.util.List;

import rx.Observable;

interface GithubRepositoryService {

    Observable<List<Repository>> getRepositoriesFrom(String organisation);

}
