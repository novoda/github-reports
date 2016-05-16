package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.RepositoriesListener;
import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;

public class RepositoriesService implements GithubRepositoryService {

    private GithubService githubService;

    public static RepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new RepositoriesService(githubServiceFactory.createService());
    }

    RepositoriesService(GithubService githubService) {
        this.githubService = githubService;
    }

    @Override
    public void getRepositories(String organisation, RepositoriesListener listener) {
        Call<List<Repository>> call = githubService.getRepositories(organisation);

        call.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                listener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                listener.onError(t);
            }
        });
    }

    @Override
    public Observable<List<Repository>> getRepositoriesFrom(String organisation) {
        return githubService.getRepositoriesFrom(organisation);
    }
}
