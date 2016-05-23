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

//        githubApiService.getRepositoriesResponseForPage(organisation, null)
//                .concatMap((Func1<Response<List<Repository>>, Observable<Response<List<Repository>>>>) response -> {
//                    Integer page = checkForRels(response);
//                    return githubApiService.getRepositoriesResponseForPage(organisation, page);
//                })
                f(organisation, 1)
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

    Observable<Response<List<Repository>>> f(String org, Integer page) {

        if (page == null) {
            return null;
        }

        return githubApiService.getRepositoriesResponseForPage(org, page)
                .concatMap(new Func1<Response<List<Repository>>, Observable<Response<List<Repository>>>>() {
                    @Override
                    public Observable<Response<List<Repository>>> call(Response<List<Repository>> response) {
                        Integer page = checkForRels(response);
                        return f(org, page);
                    }
                });
    }

    private Integer checkForRels(Response response) {
        // check if there's 'rels'
        String linkHeader = response.headers().get("Link");
        //String linkHeader = response.header("Link");
        if (linkHeader == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\?page=(\\d)>; rel=\"next\"");
        Matcher matcher = pattern.matcher(linkHeader);
        while (matcher.find()) {
            String group = matcher.group(1);
            System.out.println(">>> " + group);
            return Integer.parseInt(group);
        }

        return null; // FIXME: 23/05/2016 too many return pts
    }
}
