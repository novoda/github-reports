package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.network.DateToISO8601Converter;
import com.novoda.github.reports.batch.network.GithubApiService;
import com.novoda.github.reports.batch.network.GithubServiceContainer;
import com.novoda.github.reports.batch.network.PagedTransformer;
import com.novoda.github.reports.batch.network.RateLimitDelayTransformer;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

class GithubIssueService implements IssueService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;
    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;
    private static final Date NO_SINCE_DATE = null;

    private final GithubApiService githubApiService;
    private final DateToISO8601Converter dateConverter;
    private final RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer;
    private final RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer;
    private final RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer;

    public static IssueService newInstance() {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        return new GithubIssueService(githubApiService,
                                      dateConverter,
                                      issueRateLimitDelayTransformer,
                                      eventRateLimitDelayTransformer,
                                      commentRateLimitDelayTransformer);
    }

    private GithubIssueService(GithubApiService githubApiService,
                               DateToISO8601Converter dateConverter,
                               RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer,
                               RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer,
                               RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer) {
        this.githubApiService = githubApiService;
        this.dateConverter = dateConverter;
        this.issueRateLimitDelayTransformer = issueRateLimitDelayTransformer;
        this.eventRateLimitDelayTransformer = eventRateLimitDelayTransformer;
        this.commentRateLimitDelayTransformer = commentRateLimitDelayTransformer;
    }

    @Override
    public Observable<GithubIssue> getIssuesFor(String organisation, String repository) {
        return getPagedIssuesFor(organisation, repository, NO_SINCE_DATE, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    @Override
    public Observable<GithubIssue> getIssuesFor(String organisation, String repository, Date since) {
        return getPagedIssuesFor(organisation, repository, since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<GithubIssue>>> getPagedIssuesFor(String organisation,
                                                                      String repository,
                                                                      Date since,
                                                                      Integer page,
                                                                      Integer pageCount) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return githubApiService.getIssuesResponseForPage(organisation, repository, DEFAULT_STATE, date, page, pageCount)
                .compose(issueRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedIssuesFor(organisation, repository, since, nextPage, pageCount)));
    }

    @Override
    public Observable<GithubEvent> getEventsFor(String organisation, String repository, Integer issueNumber) {
        return getPagedEventsFor(organisation, repository, issueNumber, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    @Override
    public Observable<GithubEvent> getEventsFor(String organisation, String repository, Integer issueNumber, Date since) {
        return getPagedEventsFor(organisation, repository, issueNumber, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .filter(event -> since == null || event.getCreatedAt().after(since));
    }

    private Observable<Response<List<GithubEvent>>> getPagedEventsFor(String organisation,
                                                                      String repository,
                                                                      Integer issueNumber,
                                                                      Integer page,
                                                                      Integer pageCount) {

        return githubApiService.getEventsResponseForIssueAndPage(organisation, repository, issueNumber, page, pageCount)
                .compose(eventRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedEventsFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

    @Override
    public Observable<GithubComment> getCommentsFor(String organisation, String repository, Integer issueNumber) {
        return getPagedCommentsFor(organisation, repository, issueNumber, NO_SINCE_DATE, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    @Override
    public Observable<GithubComment> getCommentsFor(String organisation, String repository, Integer issueNumber, Date since) {
        return getPagedCommentsFor(organisation, repository, issueNumber, since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<GithubComment>>> getPagedCommentsFor(String organisation,
                                                                          String repository,
                                                                          Integer issueNumber,
                                                                          Date since,
                                                                          Integer page,
                                                                          Integer pageCount) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return githubApiService.getCommentsResponseForIssueAndPage(organisation, repository, issueNumber, date, page, pageCount)
                .compose(commentRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedCommentsFor(organisation,
                                                                                      repository,
                                                                                      issueNumber,
                                                                                      since,
                                                                                      nextPage,
                                                                                      pageCount)));
    }
}
