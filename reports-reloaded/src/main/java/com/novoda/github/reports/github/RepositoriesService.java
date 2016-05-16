package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.RepositoriesListener;
import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class RepositoriesService implements GithubRepositoryService {

    private GithubService githubService;

    public static RepositoriesService newInstance() {
        GithubService githubService = GithubServiceContainer.INSTANCE.githubService();
        return new RepositoriesService(githubService);
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
    public void getRepositoriesFrom(String organisation) {
        githubService.getRepositoriesFrom(organisation)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .subscribe(new Subscriber<List<Repository>>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Repository> repositories) {
                        System.out.println("onNext");
                        repositories.forEach(repository -> System.out.println(repository.getName()));
                    }
                });
    }
}
