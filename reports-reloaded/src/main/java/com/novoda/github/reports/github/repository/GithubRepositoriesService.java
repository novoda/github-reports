package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

class GithubRepositoriesService implements RepositoryService {

    private GithubApiService githubApiService;

    static GithubRepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new GithubRepositoriesService(githubServiceFactory.createService());
    }

    private GithubRepositoriesService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<List<Repository>> getRepositoriesFrom(String organisation) {
        return githubApiService.getRepositoriesFrom(organisation);
    }

    void getRepositoriesResponsesFrom(String organisation) {

        githubApiService.getRepositoriesResponseFrom(organisation)
                .map(new Func1<Response, Response>() {
                    @Override
                    public Response call(Response response) {

                        // check if there's 'rels'
                        String linkHeader = response.headers().get("Link");
                        //String linkHeader = response.header("Link");
                        if (linkHeader == null) {
                            return response;
                        }

                        Pattern pattern = Pattern.compile("\\?page=(\\d)>; rel=\"next\"");
                        Matcher matcher = pattern.matcher(linkHeader);
                        while (matcher.find()) {
                            String group = matcher.group(1);
                            System.out.println(">>> "+group);
                        }

                        return response;
                    }
                })
                .toBlocking()
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(Response response) {
                        List<Repository> repositories = (List<Repository>) response.body();
                        System.out.println("+++ "+repositories.size());
                    }
                });


    }
}
