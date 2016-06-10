package com.novoda.github.reports.batch.aws.repository;

import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.service.repository.RepositoryService;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class RepositoryServiceClient {

    private final RepositoryService repositoryService;

    public Observable<List<GithubRepository>> getRepositoriesFor(String organisation, int page) {
        
    }

}
