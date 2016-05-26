package com.novoda.github.reports.github.timeline;

import com.novoda.github.reports.github.PagedTransformer;
import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

class GithubTimelineService implements TimelineService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

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
        return getTimelineFor(organisation, repository, issueNumber, 1, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Event>>> getTimelineFor(String organisation,
                                                             String repository,
                                                             Integer issueNumber,
                                                             Integer page,
                                                             Integer pageCount) {
        
        return githubApiService.getTimelineFor(organisation, repository, issueNumber, page, pageCount)
                .compose(PagedTransformer.newInstance(nextPage -> getTimelineFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

}
