package com.novoda.github.reports.service.repository;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public interface RepositoryService {

    Observable<Response<List<GithubRepository>>> getRepositoriesFor(String organisation, int page, int pageCount);

}
