package com.novoda.github.reports.github.timeline;

import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

class GithubTimelineService implements TimelineService {

    private final GithubApiService githubApiService;

    public static GithubTimelineService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new GithubTimelineService(githubServiceFactory.createService());
    }

    private GithubTimelineService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<Event> getTimelineFor(String organisation, String repository, Integer issueNumber) {

        Observable<Event> result =
                githubApiService.getTimelineFor(organisation, repository, issueNumber)
                        .map(new Func1<Response<List<Event>>, List<Event>>() {
                            @Override
                            public List<Event> call(Response<List<Event>> listResponse) {
                                return listResponse.body();
                            }
                        })
                        .flatMapIterable(new Func1<List<Event>, Iterable<Event>>() {
                            @Override
                            public Iterable<Event> call(List<Event> events) {
                                return events;
                            }
                        });

        return result;
    }

}
