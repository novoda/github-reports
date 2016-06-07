package com.novoda.github.reports.batch.timeline;

import com.novoda.github.reports.batch.network.GithubApiService;
import com.novoda.github.reports.batch.network.GithubServiceContainer;
import com.novoda.github.reports.batch.network.PagedTransformer;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

@Deprecated
class GithubTimelineService implements TimelineService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final GithubApiService githubApiService;

    public static GithubTimelineService newInstance() {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService();
        return new GithubTimelineService(githubApiService);
    }

    private GithubTimelineService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<TimelineEvent> getTimelineFor(String organisation, String repository, Integer issueNumber) {
        return getTimelineFor(organisation, repository, issueNumber, 1, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<TimelineEvent>>> getTimelineFor(String organisation,
                                                                     String repository,
                                                                     Integer issueNumber,
                                                                     Integer page,
                                                                     Integer pageCount) {

        return githubApiService.getTimelineFor(organisation, repository, issueNumber, page, pageCount)
                .compose(PagedTransformer.newInstance(nextPage -> getTimelineFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

}
