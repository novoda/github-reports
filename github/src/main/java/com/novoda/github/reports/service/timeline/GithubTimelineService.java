package com.novoda.github.reports.service.timeline;

import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubServiceContainer;
import com.novoda.github.reports.service.network.PagedTransformer;
import retrofit2.Response;
import rx.Observable;

import java.util.List;

/**
 * @see TimelineService
 */
@Deprecated
public class GithubTimelineService implements TimelineService {

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
    public Observable<TimelineEvent> getTimelineFor(String organisation, String repository, int issueNumber) {
        return getTimelineFor(organisation, repository, issueNumber, 1, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<TimelineEvent>>> getTimelineFor(String organisation,
                                                                     String repository,
                                                                     int issueNumber,
                                                                     int page,
                                                                     int pageCount) {

        return githubApiService.getTimelineFor(organisation, repository, issueNumber, page, pageCount)
                .compose(PagedTransformer.newInstance(nextPage -> getTimelineFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

}
