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
        return githubApiService.getTimelineFor(organisation, repository, issueNumber, 1, 100)
                        .map(Response::body)
                        .flatMapIterable((Func1<List<Event>, Iterable<Event>>) events -> events);
    }

}
